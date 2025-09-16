package employee_self_service.user_service.external;

import employee_self_service.user_service.dto.UserPayloadDTO;
import employee_self_service.user_service.exception.BadRequestException;
import employee_self_service.user_service.exception.NotFoundException;
import employee_self_service.user_service.models.RoleSetup;
import employee_self_service.user_service.repo.RoleSetupRepo;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class KeycloakService {

    private final KeycloakConfig keycloakConfig;
    private final RoleSetupRepo roleSetupRepo;
    private RealmResource realmInstance;

    @Autowired
    public KeycloakService(KeycloakConfig keycloakConfig, RoleSetupRepo roleSetupRepo) {
        this.keycloakConfig = keycloakConfig;
        this.realmInstance = keycloakConfig.getInstance().realm("travelManagement");
        this.roleSetupRepo = roleSetupRepo;
    }


    private CredentialRepresentation credentialRepresentation(String password){
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(true);
        credentialRepresentation.setValue(password);

        return credentialRepresentation;
    }

    /**
     * @description This method is used to user records in to keycloak.
     * @param user
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 30h May 2025
     */
    public void saveUserToKeycloak(UserPayloadDTO user) {

        /**
         * Prepare data to save user details
         */
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation(user.getPassword())));
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setEmailVerified(true);

        /**
         * saving data
         */
        Response response = realmInstance.users().create(userRepresentation);
        log.info("Keycloak create user response status: {}", response.getStatus());

        if (response.getStatus() != 201) {
            throw new BadRequestException("Failed to create user");
        }

        // Extract created userId
        String userId = CreatedResponseUtil.getCreatedId(response);

        assignRole(userId, user.getRole());

    }

    /**
     * @description This method is used to update user records in keycloak given the id and the details to be updated.
     * @param userDto
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 27h May 2025
     */
    public void updateUserInKeycloak(UserPayloadDTO userDto) {
        /**
         * Fetch the existing user
         * Search keycloak by user email to get user keycloak id
         */
        List<UserRepresentation> userKeycloakData = realmInstance.users().search(userDto.getEmail(), 0,10);
        log.info("DATA:->>>>>{}", userKeycloakData.get(0).getId());
        UserResource userResource = realmInstance.users().get(userKeycloakData.get(0).getId());
        UserRepresentation userRepresentation = userResource.toRepresentation();

        /**
         * Update fields
         */
        userRepresentation.setUsername(userDto.getUsername());
        userRepresentation.setFirstName(userDto.getFirstName());
        userRepresentation.setLastName(userDto.getLastName());
        userRepresentation.setEmail(userDto.getEmail());
        userRepresentation.setEmailVerified(true);

//        // Update password (optional)
//        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
//            CredentialRepresentation credential = credentialRepresentation(userDto.getPassword());
//            userResource.resetPassword(credential);
//        }

        log.info("user representation:->>>{}", userRepresentation.getEmail()+userRepresentation.getFirstName());
        // Update user in Keycloak
        userResource.update(userRepresentation);

        // Get currently assigned roles
        List<RoleRepresentation> currentRoles = userResource.roles().realmLevel().listAll();

        // Remove all current roles
        userResource.roles().realmLevel().remove(currentRoles);

        // Add the new role
        assignRole(userKeycloakData.get(0).getId(), userDto.getRole());

        log.info("User {} updated with new role {}", userDto.getUsername(), getRoleName(userDto.getRole()));
    }

    /**
     * @description This method is used to remove user record from keycloak given the user email.
     * @param email
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 27h May 2025
     */
    public void removeUserFromKeyCloak(String email){
        if (email ==null){
            throw new BadRequestException("email cannot be null to search for user in keycloak");
        }
        List<UserRepresentation> userRepresentations = realmInstance.users().search(email, 0,10);
        if (!userRepresentations.isEmpty()){
            realmInstance.users().get(userRepresentations.get(0).getId()).remove();
        }
    }

    /**
     * @description a helper method used to assign a role to user in keycloak given the user keycloak id and role id from db.
     * @param userId
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 30th May 2025
     */
    private void assignRole(String userId, UUID roleId){

        /**
         * Fetch the role by name from keycloak
         */
        RoleRepresentation role = realmInstance.roles().get(getRoleName(roleId)).toRepresentation();

        /**
         * Assign role to user
         */
        if (role!=null){
            realmInstance.users()
                    .get(userId)
                    .roles()
                    .realmLevel()
                    .add(List.of(role));
        }

        log.info("Assigned role {} to user {}", role.getName(), userId);
    }

    /**
     * @description This method is used to save a new role in keycloak.
     * @param roleSetup
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 30h May 2025
     */
    public void saveRoleToKeycloak(RoleSetup roleSetup){
        List<RoleRepresentation> roleRepresentations = realmInstance.roles().list();
        // checking if role already exist
        roleRepresentations.forEach((role)->{
            if ( roleSetup.getName().equals(role.getName())){
                throw new NotFoundException("role already exist");
            }
        });

        // preparing role details to be saved to keycloak
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(roleSetup.getName());
        roleRepresentation.setClientRole(true);

        realmInstance.roles().create(roleRepresentation);
    }

    /**
     * @description This method is used to remove a role setup from keycloak.
     * @param role
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 30h May 2025
     */
    public void removeRoleFromKeycloak(String role){
        RoleResource roleResource = realmInstance.roles().get(role);
        roleResource.remove();
    }

    /**
     * @description This method is used to update a role setup from keycloak.
     * @param role
     * @param updatedRole
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 30h May 2025
     */
    public void updateRole(String role, String updatedRole){
        RoleResource roleResource = realmInstance.roles().get(role);
        RoleRepresentation roleRepresentation = roleResource.toRepresentation();
        roleRepresentation.setName(updatedRole);
        roleResource.update(roleRepresentation);
    }

    /**
     * @description a helper method to retrieve role name given the id
     * @param roleId
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 30th May 2025
     */
    private String getRoleName(UUID roleId){
        RoleSetup roleSetup = roleSetupRepo.findById(roleId)
                .orElseThrow(()-> new NotFoundException("role record not found"));
        return roleSetup.getName();
    }


//    @Scheduled(fixedRate = 1000)
    public List<UserRepresentation> fetchKeycloakUsers(){
        log.info("in fetch keycloak users method:->>>>");
        List<UserRepresentation> users= realmInstance.users().list();

        if (users.isEmpty()){
            throw new NotFoundException("no user record found");
        }
        return users;
    }

//   @Scheduled(fixedRate = 1000)
    public List<RoleRepresentation> fetchRealmRoles(){
        List<RoleRepresentation> roles= realmInstance.roles().list();

        if (roles.isEmpty()){
            throw new NotFoundException("no user record found");
        }

        roles.forEach((role)->{
            log.info("Keycloak roles:->>>>>>{}", role.getName());
        });
        return roles;
    }
}

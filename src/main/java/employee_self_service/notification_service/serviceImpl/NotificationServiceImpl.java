package employee_self_service.notification_service.serviceImpl;

import employee_self_service.notification_service.dto.LeaveDTO;
import employee_self_service.notification_service.service.NotificationService;
import employee_self_service.user_service.exception.BadRequestException;
import employee_self_service.user_service.exception.NotFoundException;
import employee_self_service.user_service.exception.ServerException;
import employee_self_service.user_service.models.User;
import employee_self_service.user_service.repo.UserRepo;
import employee_self_service.util.AppConstants;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final UserRepo userRepo;

    @Autowired
    public NotificationServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine, UserRepo userRepo) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.userRepo = userRepo;
    }


    /**
     * @description this method is used to notify user either on
     * Application Rejection, Application Approval, Application Confirmation
     * @param leaveDTO the payload to be sent to the notification template
     * @atuher Emmanuel Yidana
     * @createdAt 31 August 2025
     */
    @Override
    public void alertUser(LeaveDTO leaveDTO){
        try {

            /**
             * loading the user data from the db by the user email
             */
            Optional<User> userOptional = userRepo.findById(leaveDTO.getUserId());
            if (userOptional.isEmpty()){
                log.info("No user found with the ID provided->>>{}",leaveDTO.getUserId());
                throw new NotFoundException("No user record found with the email provide");
            }
            User user = userOptional.get();

            /**
             * setting email items
             */
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("eyidana001@gmail.com");
            helper.setTo(user.getEmail());

            /**
             * setting variables values to be passed to the template
             */
            Context context = new Context();
            context.setVariable("name", user.getFirstName()+user.getLastName());
            context.setVariable("status", leaveDTO.getStatus()!=null?leaveDTO.getStatus():null);
            context.setVariable("startDate", leaveDTO.getStartDate()!=null?leaveDTO.getStartDate():null);
            context.setVariable("endDate", leaveDTO.getEndDate()!=null?leaveDTO.getEndDate():null);
            context.setVariable("leaveDays", leaveDTO.getLeaveDays()!=null?leaveDTO.getLeaveDays():null);
            context.setVariable("leaveNumber", leaveDTO.getLeaveNumber()!=null?leaveDTO.getLeaveNumber():null);

            /**
             * determine which template to use base on status
             */
            if (leaveDTO.getStatus().equalsIgnoreCase(AppConstants.APPROVED)){
                log.info("Sending application approval notification->>{}", leaveDTO.getStatus());
                helper.setSubject("Application Decision");
                String htmlContent = templateEngine.process("LeaveApprovedTemplate", context);
                helper.setText(htmlContent, true);
            } else if (leaveDTO.getStatus().equalsIgnoreCase(AppConstants.REJECTED)) {
                log.info("Sending application rejection notification->>{}", leaveDTO.getStatus());
                helper.setSubject("Application Decision");
                String htmlContent = templateEngine.process("LeaveRejectedTemplate", context);
                helper.setText(htmlContent, true);
            }else if (leaveDTO.getStatus().equalsIgnoreCase(AppConstants.CANCELLED)) {
                log.info("Sending application cancellation notification->>{}", leaveDTO.getStatus());
                helper.setSubject("Leave Application");
                String htmlContent = templateEngine.process("LeaveCancelledTemplate", context);
                helper.setText(htmlContent, true);
            }else if (leaveDTO.getStatus().equalsIgnoreCase(AppConstants.PENDING_MANAGER_APPROVAL)){
                log.info("Sending application confirmation notification->>{}", leaveDTO.getStatus());
                helper.setSubject("Application Confirmation");
                String htmlContent = templateEngine.process("LeaveNotificationTemplate", context);
                helper.setText(htmlContent, true);
           }else {
                log.error("Status provided does not exist:->>{}", leaveDTO.getStatus());
                throw new BadRequestException("Status does not exist");
            }

            /**
             * send notification here
             */
            mailSender.send(message);
            log.info("Application confirmation sent to:->>{}", user.getEmail());

        } catch (Exception e) {
            log.info("Error message->>{}", e.getMessage());
            throw new ServerException("Error occurred while trying to send notification");
        }
    }

    @Override
    public void alertManager(LeaveDTO leaveDTO) {
        try {
            /**
             * loading the user data from the db by the user id
             */
            Optional<User> userOptional = userRepo.findById(leaveDTO.getUserId());
            if (userOptional.isEmpty()){
                log.info("No user found with the ID provided->>{}",leaveDTO.getUserId());
                throw new NotFoundException("No user record found with the email provide");
            }
            User user = userOptional.get();

            /**
             * get manager details
             */
            Optional<User> managerOptional = userRepo.findById(user.getManagerId());
            if (managerOptional.isEmpty()){
                log.info("Manager record does not exist:->>{}",leaveDTO.getUserId());
                throw new NotFoundException("Manager record does not exist");
            }
            User manager = managerOptional.get();

            /**
             * setting email items
             */
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("eyidana001@gmail.com");
            helper.setTo(manager.getEmail());

            /**
             * setting variables values to be passed to the template
             */
            Context context = new Context();
            context.setVariable("name", manager.getFirstName()+manager.getLastName());
            context.setVariable("status", leaveDTO.getStatus()!=null?leaveDTO.getStatus():null);
            context.setVariable("startDate", leaveDTO.getStartDate()!=null?leaveDTO.getStartDate():null);
            context.setVariable("endDate", leaveDTO.getEndDate()!=null?leaveDTO.getEndDate():null);
            context.setVariable("leaveDays", leaveDTO.getLeaveDays()!=null?leaveDTO.getLeaveDays():null);
            context.setVariable("leaveNumber", leaveDTO.getLeaveNumber()!=null?leaveDTO.getLeaveNumber():null);
            context.setVariable("firstName", user.getFirstName());
            context.setVariable("lastName", user.getLastName());

            log.info("Sending application approval notification to manager:->>{}", leaveDTO.getStatus());
            helper.setSubject("Leave Application Decision");
            String htmlContent = templateEngine.process("LeaveApprovalTemplate", context);
            helper.setText(htmlContent, true);

            /**
             * send notification here
             */
            mailSender.send(message);
            log.info("Application confirmation sent to:->>>{}", manager.getEmail());

        }catch (Exception e) {
            log.info("Error message->>>{}", e.getMessage());
            throw new ServerException("Error occurred while trying to send notification");
        }
    }

    @Override
    public void alertHR(LeaveDTO leaveDTO) {
        try {
            /**
             * loading the user data from the db by the user id
             */
            Optional<User> userOptional = userRepo.findById(leaveDTO.getUserId());
            if (userOptional.isEmpty()){
                log.info("No user found with the ID provided->>{}",leaveDTO.getUserId());
                throw new NotFoundException("No user record found with the email provide");
            }
            User user = userOptional.get();

            /**
             * get HR details
             */
            UUID employeeCompanyId = userRepo.getEmployeeCompanyId(user.getId());
            log.info("Fetching employee company Id:->>{}", employeeCompanyId);
            UUID hrId = userRepo.getHRIdByEmployeeCompanyId(employeeCompanyId);
            log.info("Fetching HR id from db:->>{}", hrId);
            Optional<User> hrOptional = userRepo.findById(hrId);
            if (hrOptional.isEmpty()){
                log.info("HR record does not exist:->>{}",leaveDTO.getUserId());
                throw new NotFoundException("HR record does not exist");
            }
            User HR = hrOptional.get();

            /**
             * setting email items
             */
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("eyidana001@gmail.com");
            helper.setTo(HR.getEmail());

            /**
             * setting variables values to be passed to the template
             */
            Context context = new Context();
            context.setVariable("name", HR.getFirstName()+HR.getLastName());
            context.setVariable("status", leaveDTO.getStatus()!=null?leaveDTO.getStatus():null);
            context.setVariable("startDate", leaveDTO.getStartDate()!=null?leaveDTO.getStartDate():null);
            context.setVariable("endDate", leaveDTO.getEndDate()!=null?leaveDTO.getEndDate():null);
            context.setVariable("leaveDays", leaveDTO.getLeaveDays()!=null?leaveDTO.getLeaveDays():null);
            context.setVariable("leaveNumber", leaveDTO.getLeaveNumber()!=null?leaveDTO.getLeaveNumber():null);
            context.setVariable("firstName", user.getFirstName());
            context.setVariable("lastName", user.getLastName());

            log.info("Sending application approval notification to HR:->>{}", leaveDTO.getStatus());
            helper.setSubject("Leave Application Decision");
            String htmlContent = templateEngine.process("LeaveApprovalTemplate", context);
            helper.setText(htmlContent, true);

            /**
             * send notification here
             */
            mailSender.send(message);
            log.info("Leave approval notification sent to HR:->>{}", HR.getEmail());

        }catch (Exception e) {
            log.info("Error message->>>{}", e.getMessage());
            throw new ServerException("Error occurred while trying to send notification");
        }
    }

}
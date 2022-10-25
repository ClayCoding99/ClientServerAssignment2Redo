package exceptions;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.JoinPoint;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.io.IOException;

@Aspect
public class AspectLogger {
    Logger logger;

    @Before("execution(public static void main(..))")
    public void beforeMain() {
        try {
            String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
            String fileName = "ErrorLog-" + formattedDate;
            String filePath = "./log/" + fileName + ".log";

            logger = Logger.getLogger(fileName);
            FileHandler fh;

            // This block configure the logger with handler and formatter
            fh = new FileHandler(filePath);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            // the following statement is used to log any messages
            logger.info("Starting Server");

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sees all packages
    @Pointcut("within(*) && execution(* *(..))")

    public void matchAllMyMethods() {
    }

    /**
     * Sees exceptions thrown by methods that throw exceptions
     */
    @AfterThrowing(value = "matchAllMyMethods()", throwing = "exception")
    public void exceptionLog(JoinPoint joinPoint, Throwable exception) {
        System.out.println("AspectJ " + exception);
        // TODO: log exception to file
        logger.info(exception.getMessage());
    }
}

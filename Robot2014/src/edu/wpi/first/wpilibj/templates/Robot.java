package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;
import org.badgerbots.lib.XBoxController;
import org.badgerbots.lib.drive.Drive;
import org.badgerbots.lib.drive.TankDriveJoy;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends SimpleRobot {

    private final Jaguar leftMotor;
    private final Jaguar rightMotor;
    private final Joystick leftJoy;
    private final Joystick rightJoy;
    private final XBoxController xbox;
    private final Drive drive;
    
    private final DoubleSolenoid launcherA;
    private final DoubleSolenoid launcherB;
    

    public Robot() {
        leftMotor = new Jaguar(1);
        rightMotor = new Jaguar(2);

        leftJoy = new Joystick(1);
        rightJoy = new Joystick(2);

        xbox = new XBoxController(3);

        drive = new TankDriveJoy(leftMotor, rightMotor, 2.0, 0.1, 1.0, 0.2, leftJoy, rightJoy);

        revButtonPressed = false;
        
        launcherA = new DoubleSolenoid(2, 1);
        launcherA.set(DoubleSolenoid.Value.kReverse);
        launcherB = new DoubleSolenoid(4, 3);
        launcherB.set(DoubleSolenoid.Value.kReverse);
    }

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() 
    {   
        leftMotor.set(.5);
        rightMotor.set(.5);
        boolean running = true;
        double newTime = Timer.getFPGATimestamp();
        double initialTime = Timer.getFPGATimestamp();
        System.out.println(rightMotor.get());
        System.out.println(rightMotor.get());
        
        while(newTime <= 5)
        {
            newTime = Timer.getFPGATimestamp() - initialTime;
        }
        
        if(newTime >= 5)
        {
            leftMotor.set(0.0);
            rightMotor.set(0.0);
            System.out.println(leftMotor.get());
            System.out.println(rightMotor.get());
            launcherA.set(DoubleSolenoid.Value.kForward);
            launcherB.set(DoubleSolenoid.Value.kForward);
        }
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        while (isOperatorControl()) {
            if (!revButtonPressed && leftJoy.getRawButton(2) && rightJoy.getRawButton(2) ) {
                drive.reverse();
                System.out.println("Robot now reversed");
            }
            revButtonPressed = leftJoy.getRawButton(2) && rightJoy.getRawButton(2);
            drive.drive();
            
            if (xbox.getButtonY()) { // if you press Y button on xbox controller
                launcherA.set(DoubleSolenoid.Value.kForward); // extend the solenoids
                launcherB.set(DoubleSolenoid.Value.kForward); // extend the solenoids
            } else if (xbox.getButtonA()) { // if you press A button on xbox controller
                launcherA.set(DoubleSolenoid.Value.kReverse); // retract the solenoids
                launcherB.set(DoubleSolenoid.Value.kReverse); // retract the solenoids
            } else if (xbox.getButtonB()) {
                double start = Timer.getFPGATimestamp();
                
                while(Timer.getFPGATimestamp() - start < 0.25)
                {
                    launcherA.set(DoubleSolenoid.Value.kForward);
                    launcherB.set(DoubleSolenoid.Value.kForward);
                }
                
                launcherA.set(DoubleSolenoid.Value.kReverse);
                launcherB.set(DoubleSolenoid.Value.kReverse);
                
            }
        }
    }
    private boolean revButtonPressed;

    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() {

    }
}

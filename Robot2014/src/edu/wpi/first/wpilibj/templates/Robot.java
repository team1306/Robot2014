package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SimpleRobot;
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
        launcherB = new DoubleSolenoid(4, 3);
    }

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() {

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
            
            if (xbox.getButtonY()) {
                launcherA.set(DoubleSolenoid.Value.kForward);
                launcherB.set(DoubleSolenoid.Value.kForward);
            } else if (xbox.getButtonA()) {
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

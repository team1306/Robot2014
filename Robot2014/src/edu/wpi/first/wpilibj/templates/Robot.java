package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import org.badgerbots.lib.PIDMotor;
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

    private final SpeedController leftMotor;
    private final SpeedController rightMotor;
    private final Joystick leftJoy;
    private final Joystick rightJoy;
    private final XBoxController xbox;
    private final Drive drive;

    private final DoubleSolenoid launcherA;
    private final DoubleSolenoid launcherB;

    private final Encoder leftEnc;
    private final Encoder rightEnc;

    public Robot() {
        xbox = new XBoxController(3);
        leftJoy = new Joystick(1);
        rightJoy = new Joystick(2);
        
        leftEnc = new Encoder(3, 4);
        leftEnc.setDistancePerPulse(0.000104);
        leftEnc.start();
        rightEnc = new Encoder(1, 2);
        rightEnc.setDistancePerPulse(0.000104);
        rightEnc.start();
        
        leftMotor = new PIDMotor(new Jaguar(1), leftEnc, 1.0, 0.0, 0.0);
        rightMotor = new PIDMotor(new Jaguar(2), rightEnc, 1.0, 0.0, 0.0);
        
        drive = new TankDriveJoy(leftMotor, rightMotor, 2.0, 0.1, 1.0, 0.2, leftJoy, rightJoy);

        revButtonPressed = false;

        launcherA = new DoubleSolenoid(2, 1);
        launcherA.set(DoubleSolenoid.Value.kOff);
        launcherB = new DoubleSolenoid(4, 3);
        launcherB.set(DoubleSolenoid.Value.kOff);

    }

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() {
        leftMotor.set(0.5);
        rightMotor.set(0.5);

        Timer.delay(5.0);

        leftMotor.set(0.0);
        rightMotor.set(0.0);
        launcherA.set(DoubleSolenoid.Value.kForward);
        launcherB.set(DoubleSolenoid.Value.kForward);
        Timer.delay(2.0);
        
        launcherA.set(DoubleSolenoid.Value.kReverse);
        launcherB.set(DoubleSolenoid.Value.kReverse);
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        launcherA.set(DoubleSolenoid.Value.kOff);
        launcherB.set(DoubleSolenoid.Value.kOff);

        double[] rates = new double[1000];
        int i = 0;
        while (isOperatorControl()) {

            if (!revButtonPressed && leftJoy.getRawButton(2) && rightJoy.getRawButton(2)) {
                drive.reverse();
                System.out.println("Robot now reversed");
            }
            revButtonPressed = leftJoy.getRawButton(2) && rightJoy.getRawButton(2);
            drive.drive();

            /**
             * Y sets the solenoids fully forward, taking a shot.
             */
            if (xbox.getButtonY()) { // if you press Y button on xbox controller
                launcherA.set(DoubleSolenoid.Value.kForward); // extend the solenoids
                launcherB.set(DoubleSolenoid.Value.kForward); // extend the solenoids
            } /**
             * A reverses the solenoids, retracting the launcher.
             */
            else if (xbox.getButtonA()) { // if you press A button on xbox controller
                launcherA.set(DoubleSolenoid.Value.kReverse); // retract the solenoids
                launcherB.set(DoubleSolenoid.Value.kReverse); // retract the solenoids
            } /**
             * B performs a truss toss. It maintains a heavily vertical arc
             * while not shooting very far horizontally.
             */
            else if (xbox.getButtonB() && !bButtonPressed) {

                launcherA.set(DoubleSolenoid.Value.kForward);
                launcherB.set(DoubleSolenoid.Value.kForward);
                Timer.delay(0.1);

                launcherA.set(DoubleSolenoid.Value.kReverse);
                launcherB.set(DoubleSolenoid.Value.kReverse);

            } /**
             * X performs a slower and shorter toss for horizontal passing. It
             * does this by moving only one of the cylinders.
             */
            else if (xbox.getButtonX()) {
//                double start = Timer.getFPGATimestamp();
//
//                for (double i = 0.025; i < 1.0; i = i + 0.025) {
//                    while (Timer.getFPGATimestamp() - start < i) {
//                        launcherA.set(DoubleSolenoid.Value.kForward);
//                        launcherB.set(DoubleSolenoid.Value.kForward);
//                    }
//                    while (Timer.getFPGATimestamp() - start < (i + 0.025)) {
//                        launcherA.set(DoubleSolenoid.Value.kReverse);
//                        launcherB.set(DoubleSolenoid.Value.kReverse);
//                    }
//                }
                launcherA.set(DoubleSolenoid.Value.kForward);

            } else if (xbox.getButtonStart() && !startButtonPressed) { //One cylinder with a pulse
                launcherA.set(DoubleSolenoid.Value.kForward);
                Timer.delay(0.45);

                launcherA.set(DoubleSolenoid.Value.kReverse);
            }
            startButtonPressed = xbox.getButtonStart();
            bButtonPressed = xbox.getButtonB();

            if (i >= 1000) {
                double sum = 0;
                for (int counter = 0; counter < 1000; counter++) {
                    sum += rates[counter];
                }
                i = 0;
                System.out.println(sum / 1000);
            } else {
                rates[i] = rightEnc.getRate();
                i++;
            }
        }
    }
    private boolean revButtonPressed;
    private boolean startButtonPressed;
    private boolean bButtonPressed;

    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() {

    }

    protected void disabled() {
        leftMotor.set(0.0);
        rightMotor.set(0.0);
        launcherA.set(DoubleSolenoid.Value.kReverse);
        launcherB.set(DoubleSolenoid.Value.kReverse);
    }

}

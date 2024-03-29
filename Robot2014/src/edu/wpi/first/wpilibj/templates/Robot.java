package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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

    private final Talon leftMotor;
    private final Talon rightMotor;
    private final Joystick leftJoy;
    private final Joystick rightJoy;
    private final XBoxController xbox;
    private final Drive drive;
    private final SpeedController intakeMotor;

    private final Compressor compressor;
    private final DoubleSolenoid pickerUpper;
    private final DoubleSolenoid launcherA;
    private final DoubleSolenoid launcherB;

    private final Encoder leftEnc;
    private final Encoder rightEnc;
    
    private final Servo pan;
    private final Servo tilt;

//    private final Vision vision = new Vision();
    public Robot() {
        xbox = new XBoxController(3);
        leftJoy = new Joystick(1);
        rightJoy = new Joystick(2);

        leftEnc = new Encoder(1, 2);
        leftEnc.setDistancePerPulse(0.0002);
        leftEnc.start();
        rightEnc = new Encoder(3, 4);
        rightEnc.setDistancePerPulse(0.0002);
        rightEnc.start();

        leftMotor = new Talon(1);
//        leftMotor = new PIDMotor(new Talon(1), leftEnc, 0.5, 0.0, 0.0);
        rightMotor = new Talon(2);
//        rightMotor = new PIDMotor(new Talon(2), rightEnc, 0.5, 0.0, 0.0);

        drive = new TankDriveJoy(leftMotor, rightMotor, 2.0, 0.1, 1.0, 0.2, leftJoy, rightJoy);

        revButtonPressed = false;

        compressor = new Compressor(5, 1);
        launcherA = new DoubleSolenoid(2, 1);
        launcherA.set(DoubleSolenoid.Value.kReverse);
        launcherB = new DoubleSolenoid(4, 3);
        launcherB.set(DoubleSolenoid.Value.kReverse);

        pickerUpper = new DoubleSolenoid(6, 5);
        intakeMotor = new Victor(3);
        
        pan = new Servo(4);
        tilt = new Servo(5);

//        vision = new Vision();
    }

    protected void robotInit() {
        compressor.start();
    }

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() {
        pickerUpper.set(DoubleSolenoid.Value.kForward);
        for (double startTime = Timer.getFPGATimestamp(); Timer.getFPGATimestamp() - startTime < 1.5; Timer.delay(0.05)) {
            leftMotor.set(0.5);
            rightMotor.set(-0.5);
        }

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
        launcherA.set(DoubleSolenoid.Value.kReverse);
        launcherB.set(DoubleSolenoid.Value.kReverse);

        SmartDashboard.putNumber("P Constant", 0.5);
        SmartDashboard.putNumber("D Constant", 0.0);
        SmartDashboard.putNumber("Update Interval", 10);

        double[] rates = new double[1000];

        for (int i = 0; isOperatorControl(); i++) {

//            leftMotor.setP(SmartDashboard.getNumber("P Constant"));
//            rightMotor.setP(SmartDashboard.getNumber("P Constant"));
//            leftMotor.setD(SmartDashboard.getNumber("D Constant"));
//            rightMotor.setP(SmartDashboard.getNumber("D Constant"));
//            Vision.ImageData data = vision.getData();
            SmartDashboard.putNumber("Left Joystick Position", leftJoy.getY());
            SmartDashboard.putNumber("Right Joystick Position", rightJoy.getY());
            SmartDashboard.putNumber("Left Motor Speed", leftMotor.get());
            SmartDashboard.putNumber("Right Motor Speed", rightMotor.get());
//            SmartDashboard.putNumber("Left Encoder Rate", leftMotor.getEncoderRate());
//            SmartDashboard.putNumber("Right Encoder Rate", rightMotor.getEncoderRate());
            SmartDashboard.putNumber("Left Encoder.getRate()", leftEnc.getRate());
            SmartDashboard.putNumber("Right Encoder.getRate()", rightEnc.getRate());
            SmartDashboard.putNumber("Intake Motor Speed", intakeMotor.get());
//            SmartDashboard.putNumber("distance from target", data.distance());
//            SmartDashboard.putBoolean("hot?", data.hot());

            if (!revButtonPressed && leftJoy.getRawButton(2) && rightJoy.getRawButton(2)) {
                drive.reverse();
                SmartDashboard.putString("Most recent action", "Robot now reversed");
            }
            revButtonPressed = leftJoy.getRawButton(2) && rightJoy.getRawButton(2);

            if (i % (int)SmartDashboard.getNumber("Update Interval") == 0) {
                drive.drive();
            }

            /**
             * Y sets the solenoids fully forward, taking a shot.
             */
            if (xbox.getButtonY() && pickerUpper.get().equals(DoubleSolenoid.Value.kForward)) { // if you press Y button on xbox controller and the picker upper is deployed and out of the way
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
            else if (xbox.getButtonB() && !bButtonPressed && pickerUpper.get().equals(DoubleSolenoid.Value.kForward)) {

                launcherA.set(DoubleSolenoid.Value.kForward);
                launcherB.set(DoubleSolenoid.Value.kForward);
                Timer.delay(0.2);

                launcherA.set(DoubleSolenoid.Value.kReverse);
                launcherB.set(DoubleSolenoid.Value.kReverse);

            } /**
             * X performs a slower and shorter toss for horizontal passing. It
             * does this by moving only one of the cylinders.
             */
            else if (xbox.getButtonX() && pickerUpper.get().equals(DoubleSolenoid.Value.kForward)) {
                launcherA.set(DoubleSolenoid.Value.kForward);

            } else if (xbox.getButtonStart() && !startButtonPressed && pickerUpper.get().equals(DoubleSolenoid.Value.kForward)) { //One cylinder with a pulse
                launcherA.set(DoubleSolenoid.Value.kForward);
                Timer.delay(0.45);

                launcherA.set(DoubleSolenoid.Value.kReverse);
            }
            // only retract the picker upper if the catapult is retracted and out of the way  
            if (xbox.getButtonRB() && launcherA.get() != DoubleSolenoid.Value.kForward && launcherB.get() != DoubleSolenoid.Value.kForward) {
                pickerUpper.set(DoubleSolenoid.Value.kReverse);
            } else if (xbox.getButtonLB()) {
                pickerUpper.set(DoubleSolenoid.Value.kForward);
            }

            intakeMotor.set(-xbox.getRawAxis(3));

            startButtonPressed = xbox.getButtonStart();
            bButtonPressed = xbox.getButtonB();

            if (i >= 1000) {
                double sum = 0;
                for (int counter = 0; counter < 1000; counter++) {
                    sum += rates[counter];
                }
                i -= 1000;
                SmartDashboard.putNumber("average left encoder rate", sum / 1000);
            }
            rates[i] = leftEnc.getRate();

            pan.set((leftJoy.getZ() + 1) / 2);
            tilt.set((rightJoy.getZ() + 1) / 2);
        }
    }
    private boolean revButtonPressed;
    private boolean startButtonPressed;
    private boolean bButtonPressed;

    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() {
        rightMotor.set(0.5);
    }

    protected void disabled() {
        launcherA.set(DoubleSolenoid.Value.kReverse);
        launcherB.set(DoubleSolenoid.Value.kReverse);
        while (isDisabled()) {
            leftMotor.set(0.0);
            rightMotor.set(0.0);
        }
    }

}

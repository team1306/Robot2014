/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SimpleRobot;
import org.badgerbots.lib.TankDrive;
import org.badgerbots.lib.TankDriveXbox;
import org.badgerbots.lib.XBoxController;

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
    private final TankDrive drive;

    public Robot() {
        leftMotor = new Jaguar(1);
        rightMotor = new Jaguar(2);
        
        leftJoy = new Joystick(1);
        rightJoy = new Joystick(2);
        
        xbox = new XBoxController(3);
        
        drive = new TankDriveXbox(leftMotor, rightMotor, 2.0, false, 0.1, 1.0, 0.2, xbox);
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
            drive.drive();
        }
    }

    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() {

    }
}
//test from finn

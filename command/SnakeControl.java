package org.usfirst.frc.team5002.swerve.commands;

import org.usfirst.frc.team5002.robot.Robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * SnakeControl.java -- teleop drive control code for linear movement + rotation
 */
public class SnakeControl extends Command {
	private static final double joystickDeadband = 0.10;
	private static final double maxDriveOutput = 1.0;

	public static final double LENGTH_INCHES = 14.5;
	public static final double WIDTH_INCHES = 16.5;

    // If true, then angle changes will be disabled at high speed
    public static final boolean enableAngleHold = false;

	double[] angles = new double[4];
	double[] speeds = new double[4];

    private boolean autoAlignButtonDebounce = false;
    protected void execute() {

		double fwd = (Math.abs(Robot.oi.getForwardAxis()) > joystickDeadband) ? Robot.oi.getForwardAxis() : 0.0;
		double str = (Math.abs(Robot.oi.getHorizontalAxis()) > joystickDeadband) ? Robot.oi.getHorizontalAxis() : 0.0;
		double rcw = (Math.abs(Robot.oi.getTurnAxis()) > joystickDeadband) ? Robot.oi.getTurnAxis() : 0.0;

		if(Math.abs(fwd)>1.0 || Math.abs(str)>1.0 || Math.abs(rcw)>1.0){
			return;
		}

		double r = Math.sqrt(Math.pow(LENGTH_INCHES,2) + Math.pow(WIDTH_INCHES,2));

		double a = str - rcw * (LENGTH_INCHES / r);
		double b = str + rcw * (LENGTH_INCHES / r);
		double c = fwd - rcw * (WIDTH_INCHES / r);
		double d = fwd + rcw * (WIDTH_INCHES / r);
		double maxWs;

		double spd_fr = Math.sqrt(Math.pow(b, 2) + Math.pow(c, 2));
		maxWs = spd_fr;

		double spd_fl = Math.sqrt(Math.pow(b, 2) + Math.pow(d, 2));
		maxWs = spd_fl > maxWs ? spd_fl : maxWs;

		double spd_bl = Math.sqrt(Math.pow(a, 2) + Math.pow(d, 2));
		maxWs = spd_bl > maxWs ? spd_bl : maxWs;

		double spd_br = Math.sqrt(Math.pow(a, 2) + Math.pow(c, 2));
		maxWs = spd_br > maxWs ? spd_br : maxWs;

		speeds[0] = (maxWs > 1 ? spd_bl / maxWs : spd_bl) * Robot.oi.getDriveSpeedCoefficient();
		speeds[1] = (maxWs > 1 ? spd_br / maxWs : spd_br) * Robot.oi.getDriveSpeedCoefficient();
		speeds[2] = (maxWs > 1 ? spd_fr / maxWs : spd_fr) * Robot.oi.getDriveSpeedCoefficient();
		speeds[3] = (maxWs > 1 ? spd_fl / maxWs : spd_fl) * Robot.oi.getDriveSpeedCoefficient();

		if(!enableAngleHold || Robot.oi.arcadeStick.getMagnitude() <= 0.50) {
			if((Math.abs(fwd) > 0.0)  ||
				(Math.abs(str) > 0.0) ||
				(Math.abs(rcw) > 0.0)
			) {
				angles[0] = (d==0 && a==0) ? 0.0 : (Math.atan2(a, d) * 180 / Math.PI); // front left
				angles[1] = (c==0 && a==0) ? 0.0 : (Math.atan2(a, c) * 180 / Math.PI); // front right
				angles[2] = (c==0 && b==0) ? 0.0 : (Math.atan2(b, c) * 180 / Math.PI); // back right
				angles[3] = (d==0 && b==0) ? 0.0 : (Math.atan2(b, d) * 180 / Math.PI); // back left
			}
		}

		Robot.drivetrain.setSteerDegrees(angles);
		Robot.drivetrain.setDriveSpeed(speeds);
    }

    public Teleop() {
        requires(Robot.drivetrain);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
		Robot.drivetrain.setSteerDegrees(0.0);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}

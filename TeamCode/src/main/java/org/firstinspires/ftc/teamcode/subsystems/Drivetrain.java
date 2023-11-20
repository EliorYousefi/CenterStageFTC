package org.firstinspires.ftc.teamcode.subsystems;

import com.ThermalEquilibrium.homeostasis.Controllers.Feedback.AngleController;
import com.ThermalEquilibrium.homeostasis.Controllers.Feedback.BasicPID;
import com.ThermalEquilibrium.homeostasis.Parameters.PIDCoefficients;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.util.BetterGamepad;
import org.firstinspires.ftc.teamcode.util.PIDFController;
import org.firstinspires.ftc.teamcode.util.values.Globals;
import org.firstinspires.ftc.teamcode.util.wrappers.BetterSubsystem;
import org.opencv.core.Mat;
@Config
public class Drivetrain extends BetterSubsystem {

    boolean redAlliance;

    private BetterGamepad _cGamepad1;

    private final RobotHardware robot;
    BasicPID pid;
    AngleController controller;
    PIDCoefficients coefficients;

    double frontLeftPower = 0, backLeftPower = 0, frontRightPower = 0, backRightPower = 0;
    public static double kP = 1, kI = 0, kD = 0;
    public static double targetAngle = 0;
    double rotationLock = 0;

    //Constructor
    public Drivetrain(Gamepad gamepad1, boolean redAlliance)
    {
        this.robot = RobotHardware.getInstance();

        pid = new BasicPID(coefficients);
        controller = new AngleController(pid);

        // gamepad helper to see if pressed button once
        this._cGamepad1 = new BetterGamepad(gamepad1);

        this.redAlliance = redAlliance;

        resetAngle();
    }

    @Override
    public void periodic() {
        _cGamepad1.update();

        double heading = robot.getAngle();

        double twist = _cGamepad1.right_stick_x * 1.1;

        Vector2d input = new Vector2d(
                -_cGamepad1.left_stick_y,
                -_cGamepad1.left_stick_x
        );


        if (_cGamepad1.YOnce())
        {
            rotationLock = controller.calculate(targetAngle, robot.getAngle());
        }
        else
        {
            rotationLock = 0;
        }

        input = new Rotation2d(Math.cos(heading), Math.sin(heading)).inverse().times(new Vector2d(-input.x, input.y));

        frontLeftPower = Range.clip(input.x + (twist + rotationLock) + input.y, -Globals.MAX_POWER, Globals.MAX_POWER);
        backLeftPower = Range.clip(input.x + (twist + rotationLock)  - input.y, -Globals.MAX_POWER, Globals.MAX_POWER);
        frontRightPower = Range.clip(input.x - (twist + rotationLock) - input.y, -Globals.MAX_POWER, Globals.MAX_POWER);
        backRightPower = Range.clip(input.x - (twist + rotationLock) + input.y, -Globals.MAX_POWER, Globals.MAX_POWER);

        robot.telemetry.addData("Heading", Math.toDegrees(heading));
        robot.telemetry.addData("fl", frontLeftPower);
        robot.telemetry.addData("bl", backLeftPower);
        robot.telemetry.addData("fr", frontRightPower);
        robot.telemetry.addData("br", backRightPower);
    }

    @Override
    public void read() {

    }

    @Override
    public void write() {
        robot.dtFrontLeftMotor.setPower(frontLeftPower);
        robot.dtBackLeftMotor.setPower(backLeftPower);
        robot.dtFrontRightMotor.setPower(frontRightPower);
        robot.dtBackRightMotor.setPower(backRightPower);
    }

    @Override
    public void reset() {
    }

    public void resetAngle()
    {
        // check if we are blue/red alliance and set zero angle - For centric drive
        if(!redAlliance)
        {
            robot.setImuOffset(-(Math.PI + Math.PI/2));
        }
        else if(redAlliance)
        {
            robot.setImuOffset(Math.PI + Math.PI/2);
        }
        robot.telemetry.addData("RESET", 0);
        robot.telemetry.update();
    }

}

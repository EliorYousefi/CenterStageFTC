package org.firstinspires.ftc.teamcode;

import com.ThermalEquilibrium.homeostasis.Utils.Timer;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.commands.ClawCommand;
import org.firstinspires.ftc.teamcode.commands.ElevatorCommand;
import org.firstinspires.ftc.teamcode.commands.ElevatorIncrementCommand;
import org.firstinspires.ftc.teamcode.commands.HandCommand;
import org.firstinspires.ftc.teamcode.commands.OuttakeCommand;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
import org.firstinspires.ftc.teamcode.subsystems.Hand;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.IntakeExtension;
import org.firstinspires.ftc.teamcode.subsystems.Outtake;
import org.firstinspires.ftc.teamcode.util.values.ClawSide;
import org.firstinspires.ftc.teamcode.util.values.Globals;
import org.firstinspires.ftc.teamcode.util.values.HandPoints;

@Config
@TeleOp(name = "OpMode Red")
public class OpMode extends CommandOpMode {

    private final RobotHardware robot = RobotHardware.getInstance();
    Drivetrain drivetrain;
    //Elevator elevator;
    Intake intake;
    //Outtake outtake;
    GamepadEx gamepadEx, gamepadEx2;
    IntakeExtension intakeExtension;

    double elevatorTarget = Globals.START_ELEVATOR;

    Timer timer;

    @Override
    public void initialize() {
        timer = new Timer();
        timer.reset();

        CommandScheduler.getInstance().reset();

        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry());
        Globals.IS_AUTO = false;
        Globals.IS_USING_IMU = true;

        gamepadEx = new GamepadEx(gamepad1);
        gamepadEx2 = new GamepadEx(gamepad2);

        robot.init(hardwareMap, telemetry);

        drivetrain = new Drivetrain(gamepad1, true);
        //elevator = new Elevator(gamepad2);
        //outtake = new Outtake();
        intake = new Intake();
        intakeExtension = new IntakeExtension(gamepad1);


        robot.addSubsystem(drivetrain,/* elevator,*/ intake/*, outtake*/, intakeExtension);

//        gamepadEx.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
//                    .whenPressed(new SequentialCommandGroup(
//                            new ClawCommand(claw, Claw.ClawState.CLOSED, ClawSide.BOTH),
//                            new WaitCommand(10),
//                            new ElevatorCommand(elevator, Globals.START_ELEVATOR)
//                            new OuttakeCommand(outtake, Outtake.Angle.OUTTAKE)
//                        ));
//
//        gamepadEx.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
//                .whenPressed(new SequentialCommandGroup(
//                        new ClawCommand(claw, Claw.ClawState.OPEN, ClawSide.BOTH),
//                        new WaitCommand(100),
//                        new ElevatorCommand(elevator, 0)
//                        new OuttakeCommand(outtake, Outtake.Angle.INTAKE)
//                ));
//


        while (opModeInInit()) {
            intake.setHand(Intake.Angle.TRANSFER);
            intake.setAmmo(Intake.Angle.TRANSFER);
            intakeExtension.setCurrent(IntakeExtension.ExtensionState.MANUAL);

            telemetry.addLine("Initialized");
            telemetry.update();
        }
    }

    @Override
    public void run() {
        gamepadEx.readButtons();
        gamepadEx2.readButtons();

        robot.read();

        intake.setManual(true);

        if(gamepad1.right_trigger != 0)
        {
            intake.intakeMove(gamepad1.right_trigger);
        }
        else if(gamepad1.left_trigger != 0)
        {
            intake.intakeMove(-gamepad1.left_trigger);
        }
        else
        {
            intake.intakeMove(0);
            intake.setShouldIntake(false);
        }


//        if(gamepadEx.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER) || gamepadEx2.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER))
//        {
//            elevatorTarget += Globals.ELEVATOR_INCREMENT;
//        }
//        else if(gamepadEx.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER) || gamepadEx2.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER))
//        {
//            elevatorTarget -= Globals.ELEVATOR_INCREMENT;
//        }
//
//        if(gamepad2.left_stick_y != 0)
//        {
//            elevator.setUsePID(false);
//        }
//        else
//        {
//            elevator.setUsePID(true);
//        }

        super.run();
        robot.periodic();

        telemetry.update();
        robot.write();
    }

    public void setElevatorTarget(double elevatorTarget)
    {
        this.elevatorTarget = elevatorTarget;
    }

    public double getElevatorTarget()
    {
        return elevatorTarget;
    }
}

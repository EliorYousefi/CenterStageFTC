package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.IntakeExtension;
import org.firstinspires.ftc.teamcode.subsystems.Outtake;
import org.firstinspires.ftc.teamcode.util.BetterGamepad;
import org.firstinspires.ftc.teamcode.util.ClawSide;

@Config
@TeleOp(name = "OpMode Red")
public class OpMode extends CommandOpMode {

    // robot
    private final RobotHardware robot = RobotHardware.getInstance();

    // subsystems
    Drivetrain drivetrain;
    Elevator elevator;
    Intake intake;
    Outtake outtake;
    Claw claw;
    IntakeExtension intakeExtension;

    // gamepads
    GamepadEx gamepadEx, gamepadEx2;
    BetterGamepad betterGamepad1, betterGamepad2;
    public static double delayTransfer = 300;
    public static double delayRelease = 1200;
    public static double delayGoToMid = 500;

    // variables
    double elevatorReset = 0;
    double previousElevator = 0;
    double transferTimer = 0;
    double releaseTimer = 0;
    double goToMidTimer = 0;
    int openedXTimes = 0;
    boolean retract = false;
    boolean goToMid = false;
    boolean canIntake = true;
    boolean startedDelayTransfer = false;
    public enum IntakeState {
        RETRACT,
        INTAKE,
        INTAKE_EXTEND
    }

    public enum LiftState {
        RETRACT,
        EXTRACT
    }

    IntakeState intakeState = IntakeState.RETRACT;
    LiftState liftState = LiftState.RETRACT;
    double loopTime = 0;

    @Override
    public void initialize() {
        CommandScheduler.getInstance().reset();

        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry());
        Globals.IS_USING_IMU = true;

        gamepadEx = new GamepadEx(gamepad1);
        gamepadEx2 = new GamepadEx(gamepad2);

        betterGamepad1 = new BetterGamepad(gamepad1);
        betterGamepad2 = new BetterGamepad(gamepad2);

        robot.init(hardwareMap, telemetry);

        drivetrain = new Drivetrain(gamepad1, true);
        elevator = new Elevator(gamepad2);
        outtake = new Outtake();
        claw = new Claw(this);
        intake = new Intake();
        intakeExtension = new IntakeExtension(gamepad1);

        intake.setAngle(Intake.Angle.TRANSFER);
        intakeExtension.setCurrent(IntakeExtension.ExtensionState.MANUAL);
        intake.updateClawState(Intake.ClawState.OPEN, ClawSide.BOTH);
        claw.updateState(Claw.ClawState.OPEN, ClawSide.BOTH);
        outtake.setAngle(Outtake.Angle.INTAKE);
        elevator.setAuto(false);

        intake.update();
        claw.update();
        outtake.update();

        while (opModeInInit())
        {
            telemetry.addLine("Initialized");
            telemetry.update();
            intake.update();
            claw.update();
            outtake.update();
        }
    }

    @Override
    public void run() {
        //intakeExtension.update();
        betterGamepad1.update();
        betterGamepad2.update();
        drivetrain.update();
        intake.update();
        outtake.update();

        if (gamepad2.left_stick_y != 0) {
            elevator.setUsePID(false);
        } else {
            elevator.setUsePID(true);
        }

        intakeStateMachine();
        elevatorStateMachine();

        telemetry.addData("hz ", 1000000000 / (System.nanoTime() - loopTime));
        telemetry.addData("ready", robot.has2Pixels());
        telemetry.addData("startedDelayTransfer", startedDelayTransfer);
        telemetry.addData("get time", getTime());
        telemetry.addData("delay", transferTimer);
        telemetry.addData("intakeState", intakeState.name());
        telemetry.update();
        CommandScheduler.getInstance().run();

        loopTime = System.nanoTime();
    }

    void intakeStateMachine()
    {
        switch (intakeState) {
            case RETRACT:

                if (betterGamepad1.rightBumperOnce() && !robot.has2Pixels() && canIntake) {
                    intakeState = IntakeState.INTAKE;
                } else if (gamepad1.right_trigger != 0 && !robot.has2Pixels() && canIntake) {
                    intakeState = IntakeState.INTAKE_EXTEND;
                }
                else if(liftState == LiftState.RETRACT)
                {
                    drivetrain.fast();
                }

                if(startedDelayTransfer)
                {
                    intake.move(Intake.Angle.TRANSFER);
                    startedDelayTransfer = false;

                    releaseTimer = getTime();
                }

                if((getTime() - releaseTimer) >= delayRelease && robot.has2Pixels())
                {
                    intake.updateClawState(Intake.ClawState.OPEN, ClawSide.BOTH);

                    goToMidTimer = getTime();

                    goToMid = true;
                }


                if(getTime() - goToMidTimer >= delayGoToMid && goToMid)
                {
                    intake.move(Intake.Angle.MID);
                    claw.updateState(Claw.ClawState.CLOSED, ClawSide.BOTH);

                    goToMid = false;
                }
                else if(liftState == LiftState.EXTRACT)
                {
                    intake.move(Intake.Angle.MID);
                }
                else
                {
                    claw.updateState(Claw.ClawState.OPEN, ClawSide.BOTH);
                    intake.move(Intake.Angle.TRANSFER);
                }

                break;
            case INTAKE:
                intake.move(Intake.Angle.INTAKE);

                if (gamepad1.right_trigger != 0) {
                    intakeState = IntakeState.INTAKE_EXTEND;
                } else if (betterGamepad1.rightBumperOnce()) {
                    intakeState = IntakeState.RETRACT;
                }
                claw.updateState(Claw.ClawState.OPEN, ClawSide.BOTH);


                if (robot.has2Pixels() && !startedDelayTransfer) {
                    transferTimer = getTime();

                    startedDelayTransfer = true;

                    intake.updateClawState(Intake.ClawState.CLOSE, ClawSide.BOTH);
                }
                else if(robot.isCloseLeft() && !robot.has2Pixels())
                {
                    intake.updateClawState(Intake.ClawState.CLOSE, ClawSide.LEFT);
                }
                else if(robot.isCloseRight() && !robot.has2Pixels())
                {
                    intake.updateClawState(Intake.ClawState.CLOSE, ClawSide.RIGHT);
                }
                else if(!startedDelayTransfer)
                {
                    intake.updateClawState(Intake.ClawState.OPEN, ClawSide.BOTH);
                }

                if((getTime() - transferTimer) >= delayTransfer && startedDelayTransfer)
                {
                    intakeState = IntakeState.RETRACT;
                }

                break;
            case INTAKE_EXTEND:
                intake.move(Intake.Angle.INTAKE);
                claw.updateState(Claw.ClawState.OPEN, ClawSide.BOTH);


                if (robot.has2Pixels() && !startedDelayTransfer) {
                    transferTimer = getTime();

                    startedDelayTransfer = true;

                    intake.updateClawState(Intake.ClawState.CLOSE, ClawSide.BOTH);
                }
                else if(robot.isCloseLeft() && !robot.has2Pixels())
                {
                    intake.updateClawState(Intake.ClawState.CLOSE, ClawSide.LEFT);
                }
                else if(robot.isCloseRight() && !robot.has2Pixels())
                {
                    intake.updateClawState(Intake.ClawState.CLOSE, ClawSide.RIGHT);
                }
                else if(!startedDelayTransfer)
                {
                    intake.updateClawState(Intake.ClawState.OPEN, ClawSide.BOTH);
                }

                if((getTime() - transferTimer) >= delayTransfer && startedDelayTransfer)
                {
                    intakeState = IntakeState.RETRACT;
                }

                if (gamepad1.right_trigger == 0) {
                    intakeState = IntakeState.RETRACT;
                }
                break;
            default:
                intakeState = IntakeState.RETRACT;
                break;
        }
    }

    void elevatorStateMachine()
    {
        switch (liftState) {
            case RETRACT:
                elevator.setTarget(0);
                outtake.setAngle(Outtake.Angle.INTAKE);

                canIntake = true;

                if (betterGamepad1.YOnce())
                {
                    previousElevator = getTime();
                    claw.updateState(Claw.ClawState.CLOSED, ClawSide.BOTH);
                    liftState = LiftState.EXTRACT;
                }
                break;
            case EXTRACT:
                canIntake = false;
                intakeState = IntakeState.RETRACT;

                elevator.setTarget(Elevator.BASE_LEVEL + (openedXTimes * Globals.ELEVATOR_INCREMENT));

                if ((getTime() - previousElevator) >= Globals.WAIT_DELAY_TILL_OUTTAKE) {
                    outtake.setAngle(Outtake.Angle.OUTTAKE);
                }

                drivetrain.slow();

                if(betterGamepad1.dpadRightOnce())
                {
                    claw.updateState(Claw.ClawState.OPEN, ClawSide.RIGHT);
                }
                else if(betterGamepad1.dpadLeftOnce())
                {
                    claw.updateState(Claw.ClawState.OPEN, ClawSide.LEFT);
                }

                if (betterGamepad1.AOnce() || betterGamepad1.leftBumperOnce())  {
                    openedXTimes++;
                    claw.updateState(Claw.ClawState.OPEN, ClawSide.BOTH);

                    elevatorReset = getTime();
                    retract = true;
                } else if ((getTime() - elevatorReset) >= Globals.WAIT_DELAY_TILL_CLOSE && retract) {
                    retract = false;
                    liftState = LiftState.RETRACT;
                }
                break;
            default:
                liftState = LiftState.RETRACT;
                break;
        }
    }


    double getTime()
    {
        return System.nanoTime() / 1000000;
    }


}

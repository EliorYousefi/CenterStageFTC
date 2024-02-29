package org.firstinspires.ftc.teamcode.subsystems;
import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.util.ClawSide;
import org.firstinspires.ftc.teamcode.util.wrappers.BetterSubsystem;
import org.jetbrains.annotations.NotNull;

@Config
public class Outtake implements Subsystem{

    private final RobotHardware robot;

    public static double intakeHandPivot = 0.85, intakeClawPivot = 0.105;
    public static double outtakeHandPivot = .4, outtakeClawPivot = .955;

    public static double power = 1;

    public static void setOuttakeHandPivot(double outtakeHandPivot) {
        Outtake.outtakeHandPivot = outtakeHandPivot;
    }

    @Override
    public void play() {

    }

    @Override
    public void loop(boolean allowMotors) {
        update();
    }

    @Override
    public void stop() {

    }

    public enum Angle
    {
        INTAKE,
        OUTTAKE
    }

    public enum Type
    {
        CLAW,
        HAND
    }

    Angle angle = Angle.INTAKE;
    public Outtake()
    {
        this.robot = RobotHardware.getInstance();
    }


    public void update() {
        updateState(Type.HAND);
        updateState(Type.CLAW);

    }

    public void setAngle(@NotNull Angle angle) {
        this.angle = angle;

        updateState(Type.HAND);
        updateState(Type.CLAW);
    }

    public void updateState(@NotNull Type type) {

        switch(type) {
            case CLAW:
                switch (angle){
                    case INTAKE:
                        this.robot.outtakeClawPivotServo.setPosition(intakeClawPivot);
                        break;
                    case OUTTAKE:
                        this.robot.outtakeClawPivotServo.setPosition(outtakeClawPivot);
                        break;
                }
                break;
            case HAND:
                switch (angle){
                    case INTAKE:
                        this.robot.outtakeHandRightServo.setPosition(intakeHandPivot);
                        this.robot.outtakeHandLeftServo.setPosition(intakeHandPivot);
                        break;
                    case OUTTAKE:
                        this.robot.outtakeHandRightServo.setPosition(outtakeHandPivot);
                        this.robot.outtakeHandLeftServo.setPosition(outtakeHandPivot);
                        break;
                }
                break;
        }
    }

}
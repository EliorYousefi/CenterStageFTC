package org.firstinspires.ftc.teamcode.auto.rr1.Actions;

import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.AccelConstraint;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.IntakeExtension;
import org.firstinspires.ftc.teamcode.util.ClawSide;
import org.firstinspires.ftc.teamcode.util.Stopwatch;

public class PlacePurpleActions {

    private Intake intake;
    private IntakeExtension intakeExtension;
    private Stopwatch timer;

    public PlacePurpleActions(Intake intake, IntakeExtension intakeExtension) {
        this.intake = intake;
        this.intakeExtension = intakeExtension;
        timer = new Stopwatch();
    }

    private boolean activateSystem(Runnable systemFunction, long delay, Object... parameters) {
        if (timer.hasTimePassed(delay)) {
            systemFunction.run();
            timer.reset();
            return false; // Activation successful
        } else {
            return true; // Activation failed
        }
    }

    public class PlacePurpleMid implements Action {

        public PlacePurpleMid() {
            timer.reset();
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            intake.move(Intake.Angle.INTAKE);

            return activateSystem(() -> intake.updateClawState(Intake.ClawState.OPEN, ClawSide.LEFT), 500);
        }
    }

    public class Retract implements Action {

        public Retract() {
            timer.reset();
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            intake.move(Intake.Angle.OUTTAKE);

            return false;
        }
    }


    public Action placePurpleMid() {
        return new PlacePurpleMid();
    }

    public Action retract() {
        return new Retract();
    }
}



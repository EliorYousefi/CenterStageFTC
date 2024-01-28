package org.firstinspires.ftc.teamcode.auto.old_with_cycles;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

@Config
public class AutoConstants {
    //Coordinates
    //WAIT TIME
    public static final double WAIT = .5 ;


    //Poses
    public Pose2d startPoseRedLeft = new Pose2d(-36, -62, Math.toRadians(90)); // ok
    public Pose2d startPoseRedRight = new Pose2d(12, -62, Math.toRadians(90)); // ok
    public Pose2d startPoseBlueLeft = new Pose2d(36, 62, Math.toRadians(-90)); // ok
    public Pose2d startPoseBlueRight = new Pose2d(-36, 62, Math.toRadians(-90)); // ok
    public static Pose2d currentPose = new Pose2d(0,0);
}
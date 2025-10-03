package net.danygames2014.buildcraft.config;

import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter.HandOffResult;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class PipeConfig {
    @ConfigEntry(name = "Failed Hand-Off Result", multiplayerSynced = true)
    public HandOffResult failedHandOffResult = HandOffResult.DROP;
}

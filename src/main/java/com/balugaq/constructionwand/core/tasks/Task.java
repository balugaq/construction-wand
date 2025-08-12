package com.balugaq.constructionwand.core.tasks;

import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public interface Task extends Consumer<BukkitTask> {
}

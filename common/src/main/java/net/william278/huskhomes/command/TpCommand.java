/*
 * This file is part of HuskHomes, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.huskhomes.command;

import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.teleport.*;
import net.william278.huskhomes.user.OnlineUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TpCommand extends InGameCommand implements UserListTabCompletable {

    protected TpCommand(@NotNull HuskHomes plugin) {
        super(
                List.of("tp"),
                "<player> [target]",
                plugin
        );

        addAdditionalPermissions(Map.of("other", true));
        setOperatorCommand(true);
    }

    @Override
    public void execute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        final Optional<String> optionalTarget = parseStringArg(args, 0);
        if (optionalTarget.isEmpty()) {
            plugin.getLocales().getLocale("error_invalid_syntax", getUsage())
                    .ifPresent(onlineUser::sendMessage);
            return;
        }

        switch (args.length) {
            case 1 -> {
                this.execute(onlineUser, onlineUser, Target.username(args[0]), args);
            }
            default -> this.execute(onlineUser, Teleportable.username(args[0]), Target.username(args[1]), args);
        }
    }

    // Execute a teleport
    private void execute(@NotNull OnlineUser onlineUser, @NotNull Teleportable teleporter, @NotNull Target target,
                         @NotNull String[] args) {
        // Build and execute the teleport
        final TeleportBuilder builder = Teleport.builder(plugin)
                .teleporter(teleporter)
                .target(target);

        // Determine teleporter and target names, validate permissions
        final @Nullable String targetName = target instanceof Username username ? username.name()
                : target instanceof OnlineUser online ? online.getName() : null;
        if (onlineUser.equals(teleporter)) {
            if (teleporter.getUsername().equalsIgnoreCase(targetName)) {
                plugin.getLocales().getLocale("error_cannot_teleport_self")
                        .ifPresent(onlineUser::sendMessage);
                return;
            }
        } else if (!onlineUser.hasPermission(getPermission("other"))) {
            plugin.getLocales().getLocale("error_no_permission")
                    .ifPresent(onlineUser::sendMessage);
            return;
        }
        builder.executor(onlineUser);

        // Execute teleport
        if (!builder.buildAndComplete(true, args)) {
            return;
        }

        // Display the teleport completion message
        plugin.getLocales().getLocale("teleporting_other_complete",
                        teleporter.getUsername(), Objects.requireNonNull(targetName))
                .ifPresent(onlineUser::sendMessage);
    }

}

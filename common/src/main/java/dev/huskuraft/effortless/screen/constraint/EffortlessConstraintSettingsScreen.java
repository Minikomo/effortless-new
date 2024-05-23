package dev.huskuraft.effortless.screen.constraint;

import dev.huskuraft.effortless.EffortlessClient;
import dev.huskuraft.effortless.api.gui.AbstractPanelScreen;
import dev.huskuraft.effortless.api.gui.button.Button;
import dev.huskuraft.effortless.api.gui.text.TextWidget;
import dev.huskuraft.effortless.api.platform.Entrance;
import dev.huskuraft.effortless.api.text.Text;
import dev.huskuraft.effortless.networking.packets.player.PlayerPermissionCheckPacket;
import dev.huskuraft.effortless.screen.settings.EffortlessNotAnOperatorScreen;
import dev.huskuraft.effortless.screen.settings.EffortlessSessionStatusScreen;

public class EffortlessConstraintSettingsScreen extends AbstractPanelScreen {

    public EffortlessConstraintSettingsScreen(Entrance entrance) {
        super(entrance, Text.translate("effortless.constraint_settings.title"), PANEL_WIDTH, PANEL_TITLE_HEIGHT_1 + PANEL_BUTTON_ROW_HEIGHT_3);
    }

    @Override
    public void onCreate() {
        addWidget(new TextWidget(getEntrance(), getLeft() + getWidth() / 2, getTop() + PANEL_TITLE_HEIGHT_1 - 10, getScreenTitle().withColor(0x00404040), TextWidget.Gravity.CENTER));

        addWidget(Button.builder(getEntrance(), Text.translate("effortless.global_constraint_settings.title"), button -> {
            if (!getEntrance().getSessionManager().isSessionValid()) {
                getEntrance().getClient().execute(() -> {
                    new EffortlessSessionStatusScreen(getEntrance()).attach();
                });
            } else {
                getEntrance().getChannel().sendPacket(new PlayerPermissionCheckPacket(getEntrance().getClient().getPlayer().getId()), (packet) -> {
                    if (packet.granted()) {
                        getEntrance().getClient().execute(() -> {
                            new EffortlessGlobalConstraintSettingsScreen(getEntrance(), getEntrance().getSessionManager().getServerSessionConfigOrEmpty().getGlobalConfig(), config -> {
                                getEntrance().getSessionManager().updateGlobalConfig(config);
                            }).attach();
                        });
                    } else {
                        getEntrance().getClient().execute(() -> {
                            new EffortlessNotAnOperatorScreen(getEntrance()).attach();
                        });
                    }
                });
            }

        }).setBoundsGrid(getLeft(), getTop(), getWidth(), getHeight(), 2f, 0f, 1f).build());
        addWidget(Button.builder(getEntrance(), Text.translate("effortless.player_constraint_settings.title"), button -> {
            getEntrance().getChannel().sendPacket(new PlayerPermissionCheckPacket(getEntrance().getClient().getPlayer().getId()), (packet) -> {

                if (!getEntrance().getSessionManager().isSessionValid()) {
                    getEntrance().getClient().execute(() -> {
                        new EffortlessSessionStatusScreen(getEntrance()).attach();
                    });
                } else {
                    if (packet.granted()) {
                        getEntrance().getClient().execute(() -> {
                            new EffortlessPlayerConstraintSettingsListScreen(getEntrance(), getEntrance().getSessionManager().getServerSessionConfigOrEmpty().playerConfigs(), playerConfigs -> {
                                getEntrance().getSessionManager().updatePlayerConfig(playerConfigs);
                            }).attach();
                        });
                    } else {
                        getEntrance().getClient().execute(() -> {
                            new EffortlessNotAnOperatorScreen(getEntrance()).attach();
                        });
                    }
                }
            });
        }).setBoundsGrid(getLeft(), getTop(), getWidth(), getHeight(), 1f, 0f, 1f).build());

        addWidget(Button.builder(getEntrance(), Text.translate("effortless.button.done"), button -> {
            detach();
        }).setBoundsGrid(getLeft(), getTop(), getWidth(), getHeight(), 0f, 0f, 1f).build());

    }

    @Override
    protected EffortlessClient getEntrance() {
        return (EffortlessClient) super.getEntrance();
    }
}
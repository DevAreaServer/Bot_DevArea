package devarea.automatical;

import devarea.Main;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.entity.channel.VoiceChannel;

public class HelpVoiceChannel {

    private static int number = 1;

    public static void join(VoiceStateUpdateEvent event) {
        try {
            if (event.getCurrent().getChannelId().get().equals(Main.idVoiceChannelHelp)) {
                Snowflake id = Main.devarea.createVoiceChannel(voiceChannelCreateSpec -> {
                    voiceChannelCreateSpec.setName("Aide #" + number);
                    voiceChannelCreateSpec.setParentId(Main.idCategoryGeneral);
                    voiceChannelCreateSpec.setUserLimit(5);
                    number++;
                }).block().getId();
                event.getCurrent().getMember().block().edit(guildMemberEditSpec -> {
                    guildMemberEditSpec.setNewVoiceChannel(id);
                }).block();
            }
            if (event.getOld().isPresent())
                leave(event);
        } catch (Exception ignored) {
        }
    }

    public static void leave(VoiceStateUpdateEvent event) {
        VoiceChannel channel = event.getOld().get().getChannel().block();
        if (channel.getVoiceStates().buffer().blockLast() == null && channel.getName().startsWith("Aide")) {
            number--;
            try {
                channel.delete().block();
            } catch (Exception ignored) {
            }
        }
    }

}

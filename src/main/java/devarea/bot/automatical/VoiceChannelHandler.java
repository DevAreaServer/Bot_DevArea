package devarea.bot.automatical;

import devarea.bot.Init;
import devarea.global.cache.ChannelCache;
import devarea.global.cache.MemberCache;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.spec.GuildMemberEditSpec;
import discord4j.core.spec.VoiceChannelCreateSpec;
import discord4j.discordjson.possible.Possible;

import java.util.Optional;

public class VoiceChannelHandler {

    private static int number = 1;

    public static void join(VoiceStateUpdateEvent event) {
        try {
            if (event.getCurrent().getChannelId().get().equals(Init.initial.Help_voiceChannel)) {
                MemberCache.get(event.getCurrent().getUserId().asString()).edit(
                        GuildMemberEditSpec.builder()
                                .newVoiceChannel(Possible.of(Optional.of(Init.devarea.createVoiceChannel(VoiceChannelCreateSpec.builder()
                                        .name("Aide #" + number++)
                                        .parentId(Init.initial.general_category)
                                        .userLimit(5)
                                        .build()).block().getId())))
                                .build()
                ).subscribe();
            }
            if (event.getOld().isPresent())
                leave(event);
        } catch (Exception ignored) {
        }
    }

    public static void leave(VoiceStateUpdateEvent event) {
        if (event.getOld().isEmpty() || event.getOld().get().getChannelId().isEmpty())
            return;

        VoiceChannel channel = (VoiceChannel) ChannelCache.watch(event.getOld().get().getChannelId().get().asString());
        if (channel.getVoiceStates().buffer().blockLast() == null && channel.getName().startsWith("Aide")) {
            number--;
            try {
                channel.delete().subscribe();
            } catch (Exception ignored) {
            }
        }
    }

}

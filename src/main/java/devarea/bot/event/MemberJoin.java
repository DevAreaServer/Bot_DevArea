package devarea.bot.event;

import devarea.Main;
import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.ConsumableCommand;
import devarea.bot.commands.outLine.JoinCommand;
import devarea.global.cache.ChannelCache;
import devarea.global.cache.MemberCache;
import devarea.global.handlers.XPHandler;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.object.entity.channel.GuildMessageChannel;

public class MemberJoin {

    private static GuildMessageChannel channelJoin;

    public static void memberJoinFunction(MemberJoinEvent event) {
        MemberCache.use(event.getMember());

        ((GuildMessageChannel) ChannelCache.watch(Init.initial.logJoin_channel.asString()))
                .createMessage(msg -> msg.setContent(event.getMember().getDisplayName() + " a rejoint le serveur !"))
                .subscribe();

        XPHandler.addNewMember(event.getMember().getId());

        if (!Main.developing)
            CommandManager.addManualCommand(event.getMember(), new ConsumableCommand(JoinCommand.class) {
                @Override
                protected Command command() {
                    return new JoinCommand(event.getMember());
                }
            });
    }

}

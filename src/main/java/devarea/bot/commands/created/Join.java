package devarea.bot.commands.created;

import devarea.bot.data.ColorsUsed;
import devarea.bot.Init;
import devarea.bot.commands.ShortCommand;
import devarea.bot.event.MemberJoin;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;

public class Join extends ShortCommand {

    public Join(MessageCreateEvent message) {
        super(message);
        if (message.getMessage().getAuthor().get().getId().asString().equals("321673326105985025")) {
            if (message.getMessage().getUserMentions().buffer().count().block() > 0) {
                Member member = message.getMessage().getUserMentions().blockFirst().asMember(Init.devarea.getId()).block();
                assert member != null;
                MemberJoin.join(member);
                sendEmbed(embed -> {
                    embed.setTitle("Vous avez fait join " + member.getDisplayName() + " !");
                    embed.setColor(ColorsUsed.just);
                }, false);
            }
        } else {
            sendError("Vous n'avez pas la permission d'utiliser cette commande !");
        }
    }
}
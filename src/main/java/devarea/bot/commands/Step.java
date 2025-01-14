package devarea.bot.commands;

import devarea.bot.Init;
import devarea.bot.presets.ColorsUsed;
import devarea.global.cache.ChannelCache;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.LayoutComponent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageEditSpec;

import java.util.ArrayList;

public abstract class Step implements Cloneable {

    public final static boolean end = true;
    public final static boolean next = false;

    protected Step[] steps;
    protected Message message;
    protected Step called;

    public Step(Step... steps) {
        assert steps.length != 0;
        this.steps = steps;
    }

    protected abstract boolean onCall(Message message);

    protected boolean onReceiveMessage(MessageCreateEvent event) {
        sendErrorEntry();
        return next;
    }

    protected boolean onReceiveReact(ReactionAddEvent event) {
        sendErrorEntry();
        return next;
    }

    protected boolean onReceiveInteract(ButtonInteractionEvent event) {
        event.reply(InteractionApplicationCommandCallbackSpec.builder().addEmbed(EmbedCreateSpec.builder()
                .title("Erreur !")
                .description("Votre entrée n'est pas valide !")
                .color(ColorsUsed.wrong)
                .build()).ephemeral(true).build()).subscribe();
        return next;
    }

    public boolean call(Message message) {
        this.message = message;
        return onCall(message);
    }

    public boolean receiveMessage(MessageCreateEvent event) {
        if (this.called == null)
            return onReceiveMessage(event);
        else
            return this.called.receiveMessage(event);

    }

    public boolean receiveReact(ReactionAddEvent event) {
        if (this.called == null)
            return onReceiveReact(event);
        else
            return this.called.receiveReact(event);
    }

    public boolean receiveInteract(ButtonInteractionEvent event) {
        if (this.called == null)
            return onReceiveInteract(event);
        else
            return this.called.receiveInteract(event);
    }

    protected Step step(int nb) throws Exception {
        if (nb < 0 || nb >= steps.length) {
            throw new Exception("Le numéro du step n'est pas associé !");
        }
        return steps[nb];
    }

    protected void setText(EmbedCreateSpec spec) {
        this.message.edit(MessageEditSpec.builder().addEmbed(spec).build()).subscribe();
    }

    protected void setMessage(MessageEditSpec spec) {
        this.message.edit(spec).subscribe();
    }

    protected boolean call(Step step) {
        try {
            this.called = ((Step) step.clone());
            return this.called.call(this.message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return next;
    }

    protected boolean callStep(int nb) {
        try {
            return this.call(step(nb));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return end;
    }

    protected void sendErrorEntry() {
        Command.sendError((GuildMessageChannel) ChannelCache.watch(this.message.getChannelId().asString()),
                "Votre entrée n'est pas valide !");
    }

    protected void addYesEmoji() {
        this.message.addReaction(ReactionEmoji.custom(Init.idYes)).subscribe();
    }

    protected ActionRow getYesButton() {
        return ActionRow.of(Button.primary("yes", ReactionEmoji.custom(Init.idYes)));
    }

    protected ActionRow getNoButton() {
        return ActionRow.of(Button.primary("no", ReactionEmoji.custom(Init.idNo)));
    }

    protected ActionRow getYesNoButton() {
        return ActionRow.of(Button.primary("yes", ReactionEmoji.custom(Init.idYes)), Button.primary("no",
                ReactionEmoji.custom(Init.idNo)));
    }

    protected ArrayList<LayoutComponent> getEmptyButton() {
        return new ArrayList<>();
    }

    protected void addNoEmoji() {
        this.message.addReaction(ReactionEmoji.custom(Init.idNo)).subscribe();
    }

    protected void addYesNoEmoji() {
        addYesEmoji();
        addNoEmoji();
    }

    protected void removeAllEmoji() {
        this.message.removeAllReactions().subscribe();
    }

    protected void removeNoEmoji() {
        this.message.removeReactions(ReactionEmoji.custom(Init.idNo)).subscribe();
    }

    protected void removeYesEmoji() {
        this.message.removeReactions(ReactionEmoji.custom(Init.idYes)).subscribe();
    }

    protected boolean isYes(ReactionAddEvent event) {
        return ReactionEmoji.custom(Init.idYes).equals(event.getEmoji());
    }

    protected boolean isYes(ButtonInteractionEvent event) {
        return event.getCustomId().equals("yes");
    }

    protected boolean isNo(ReactionAddEvent event) {
        return ReactionEmoji.custom(Init.idNo).equals(event.getEmoji());
    }

    protected boolean isNo(ButtonInteractionEvent event) {
        return event.getCustomId().equals("no");
    }

    protected String getContent(MessageCreateEvent event) {
        return event.getMessage().getContent();
    }

}

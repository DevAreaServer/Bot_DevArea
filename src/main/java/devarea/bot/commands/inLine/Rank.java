package devarea.bot.commands.inLine;

import devarea.bot.commands.SlashCommand;
import devarea.global.cache.MemberCache;
import devarea.bot.Init;
import devarea.global.handlers.XPHandler;
import devarea.bot.commands.ShortCommand;
import devarea.bot.presets.ColorsUsed;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class Rank extends ShortCommand implements SlashCommand {

    public Rank(final Member member, final ChatInputInteractionEvent event) {
        super(member, event);
        Member pinged = member;
        try {
            if (event.getOption("membre").isPresent() && event.getOption("membre").get().getValue().isPresent()) {
                pinged = MemberCache.get(event.getOption("membre").get().getValue().get().asSnowflake().asString());
                if (pinged == null)
                    pinged = member;
            }
        } catch (Exception e) {
        } finally {
            if (!XPHandler.haveBeenSet(pinged.getId())) {
                replyError("Ce membre n'a pas encore parlé !");
                this.endCommand();
            } else {

                try {
                    BufferedImage img = new BufferedImage(Init.assetsImages.get("xp_background").getWidth(),
                            Init.assetsImages.get("xp_background").getHeight()
                            , BufferedImage.TYPE_INT_ARGB);
                    img.getGraphics().drawImage(Init.assetsImages.get("xp_background"), 0, 0, img.getWidth(),
                            img.getHeight(), null);
                    Graphics2D g = (Graphics2D) img.getGraphics();

                    int xp = XPHandler.getXpOfMember(pinged.getId());
                    int level = XPHandler.getLevelForXp(xp);

                    float pourcentage =
                            (float) (xp - XPHandler.getAmountForLevel(level)) / (XPHandler.getAmountForLevel(level + 1) - XPHandler.getAmountForLevel(level));

                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    try {
                        g.drawImage(makeRoundedCorner(ImageIO.read(new URL(pinged.getAvatarUrl())), 600), 25, 25, 200
                                , 200, null);
                    } catch (Exception e) {
                        g.drawImage(makeRoundedCorner(ImageIO.read(new URL(pinged.getDefaultAvatarUrl())), 600), 25,
                                25, 200, 200, null);
                    }

                    g.setColor(Color.white);
                    g.setStroke(new BasicStroke(18.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, null,
                            0.0f));
                    g.draw(new Line2D.Float(250, 200, 650, 200));
                    g.setColor(new Color(120, 238, 96));
                    g.draw(new Line2D.Float(250, 200, 250 + (pourcentage * 400), 200));

                    g.setColor(Color.WHITE);
                    Font font = Font.createFont(Font.TRUETYPE_FONT,
                            Init.class.getResource("/assets/font.otf").openStream()).deriveFont(45f);
                    g.setFont(font);
                    g.drawString(level + "", 240, 182);
                    drawLeft(g, (level + 1) + "", 659, 182, g.getFont());
                    drawCenteredString(g, "xp-" + xp, 450, 182, g.getFont());
                    String name = pinged.getDisplayName() + "#" + XPHandler.getRankOfMember(pinged.getId());
                    drawLeftAndTop(g, name, 685, -10, getFontSize(name, g));

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ImageIO.write(img, "png", outputStream);
                    Member finalPinged = pinged;
                    event.reply("**Voici l'XP de " + pinged.getUsername() + "** :").subscribe();
                    this.send(MessageCreateSpec.builder()
                            .addFile("xp.png", new ByteArrayInputStream(outputStream.toByteArray()))
                            .addEmbed(EmbedCreateSpec.builder()
                                    .color(ColorsUsed.just)
                                    .image("attachment://xp.png").build()
                            ).build(), false);
                } catch (IOException | FontFormatException e) {
                    e.printStackTrace();
                }
            }
            this.endCommand();
        }
    }

    private static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();

        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);

        g2.dispose();

        return output;
    }

    private static void drawCenteredString(Graphics g, String text, int x, int y, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int posX = x - (metrics.stringWidth(text) / 2);
        g.setFont(font);
        g.drawString(text, posX, y);
    }

    private static void drawLeft(Graphics g, String text, int x, int y, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int posX = x - metrics.stringWidth(text);
        g.setFont(font);
        g.drawString(text, posX, y);
    }

    private static void drawLeftAndTop(Graphics g, String text, int x, int y, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int posX = x - metrics.stringWidth(text);
        int poxY = y + metrics.getHeight();
        g.setFont(font);
        g.drawString(text, posX, poxY);
    }

    private static Font getFontSize(String text, Graphics2D g) {
        Font font = g.getFont().deriveFont(150f);
        FontMetrics metrics = g.getFontMetrics(font);
        while (420 < metrics.stringWidth(text)) {
            font = font.deriveFont((float) (font.getSize() - 1));
            metrics = g.getFontMetrics(font);
        }
        return font;
    }

    public Rank() {
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("rank")
                .description("Donne les informations concernant l'XP d'un membre.")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("membre")
                        .description("Vous pouvez demander le membre que vous voulez. Par default ce sera vous.")
                        .type(ApplicationCommandOption.Type.MENTIONABLE.getValue())
                        .required(false)
                        .build())
                .build();
    }
}

package devarea.global.badges.role_badge;

import devarea.Main;
import devarea.bot.Init;

public class Booster_Badge extends RolesBadges {

    public Booster_Badge() {
        super("Booster", Main.domainName + "assets/images/badges/70x70/roles_badges/booster_badge.png",
                "Ce membre booste gentiment le serveur !", Init.badgesImages.get("booster_badge"));
    }

}

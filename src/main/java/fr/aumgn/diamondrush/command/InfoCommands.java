package fr.aumgn.diamondrush.command;

import org.bukkit.command.CommandSender;

import fr.aumgn.bukkitutils.command.Command;
import fr.aumgn.bukkitutils.command.CommandArgs;
import fr.aumgn.bukkitutils.command.NestedCommands;
import fr.aumgn.diamondrush.DiamondRush;
import fr.aumgn.diamondrush.game.Game;
import fr.aumgn.diamondrush.game.TeamsView;

@NestedCommands(name = "diamondrush")
public class InfoCommands extends DiamondRushCommands {

    public InfoCommands(DiamondRush dr) {
        super(dr);
    }

    @Command(name = "teams")
    public void showTeams(CommandSender sender, CommandArgs args) {
        Game game = dr.getGame();
        TeamsView view = new TeamsView(game.getTeams());
        for (String message : view) {
            sender.sendMessage(message);
        }
    }
}

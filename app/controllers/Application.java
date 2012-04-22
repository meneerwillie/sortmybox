package controllers;

import java.util.List;

import org.mortbay.log.Log;

import models.FileMove;
import models.Rule;
import models.Rule.TooManyRulesException;
import models.User;
import play.mvc.Controller;
import play.mvc.With;
import dropbox.client.DropboxClient;
import dropbox.client.DropboxClientFactory;
import dropbox.client.DropboxClient.ListingType;

/**
 * @author mustpax
 */
@With(Login.class)
public class Application extends Controller {
    public static void index() throws TooManyRulesException {
        User user = Login.getLoggedInUser();
        boolean createdSortbox = user.createSortboxAndCannedRulesIfNecessary();
        List<Rule> rules = Rule.findByOwner(user).fetch();
        List<FileMove> moves = user.getMoves().limit(10).fetch();
        render(user, rules, moves, createdSortbox);
    }
    
    public static void dirs(String path) {
        checkAuthenticity();
        DropboxClient client = DropboxClientFactory.create(Login.getLoggedInUser());
        try {
	        renderJSON(client.listDir(path, ListingType.DIRS));
        } catch (IllegalArgumentException e) {
            badRequest();
        }
    }
}

package taass.util;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import taass.model.User;
import taass.payload.News;

@Service
public class NotificationService {

    WebClient client = WebClient.create("http://217.61.56.247:19090/");
    //WebClient client = WebClient.create("http://127.0.0.1:4040");


    public NotificationService(){}

    public void sendNews(User user, Long rentID, String title, String message){
        //System.out.println(message);

        News news = new News(user.getUserName(), rentID, title, message);
        client.post().uri("/news/add").body(BodyInserters.fromValue(news)).exchange().doOnError(throwable -> {
                    System.out.println("Server notifiche NON disponibile");
                }).subscribe();

    }

    public void delNews(Long rentID){

        client.post().uri("/news/del").body(BodyInserters.fromValue(rentID)).exchange().doOnError(throwable -> {
            System.out.println("Server notifiche NON disponibile");
        }).subscribe();

    }

    public void delAllNews(){

        client.delete().uri("/news").exchange().doOnError(throwable -> {
            System.out.println("Server notifiche NON disponibile");
        }).subscribe();

    }

}



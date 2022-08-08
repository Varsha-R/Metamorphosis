
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@Builder(builderClassName = "Builder")
@Getter
@Setter
public class SlackMessage implements Serializable {

    private String username;
    private String text;
    private String icon_emoji;
}
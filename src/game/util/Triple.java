package game.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Triple<A, B, C> {

    A first;
    B second;
    C third;
}

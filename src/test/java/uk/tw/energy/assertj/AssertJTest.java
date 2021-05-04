package uk.tw.energy.assertj;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AssertJTest {
    @Mock
    Set<String> set;

    @BeforeAll
    public static void log() {
        System.out.println("BeforeAll check");
    }

    @BeforeEach
    public void logg() {
        System.out.println("BeforeEach check");
    }

    @AfterAll
    public static void loggg() {
        System.out.println("AfterAll check");
    }

    @AfterEach
    public void logggg() {
        System.out.println("AfterEach check");
    }

    @Test
    public void testMockito() {
        List<Integer> list = mock(List.class);
        when(list.get(0)).thenReturn(3);
        assertThat(list.get(0)).isEqualTo(3);

        assertThat(set.contains("Chair")).isEqualTo(false);
    }

    @Test
    public void testUserClass() {
        UserDetails usd1 = new UserDetails(1);
        UserDetails usd2 = new UserDetails(2);
        UserDetails usd3 = new UserDetails(3);
        User user1 = new User(1, "Ravi", new ArrayList<>(Arrays.asList(usd1, usd2)));
        User user2 = new User(1, "Ravi", new ArrayList<>(Arrays.asList(usd1, usd2)));
        assertThat(user1).isEqualToComparingFieldByField(user2);
    }

    @Test
    public void testUserDetailsClass() {
        UserDetails usd1 = new UserDetails(1);
        UserDetails usd2 = new UserDetails(1);
        assertThat(usd1).isEqualToComparingFieldByField(usd2);
    }
}

package unit.uk.gov.ida.verifyserviceprovider.utils;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import uk.gov.ida.verifyserviceprovider.utils.StringTableFormatter;

import static java.lang.System.lineSeparator;
import static org.assertj.core.api.Assertions.assertThat;

public class StringTableFormatterTest {

    @Test
    public void shouldFormatTableToString() {
        String expected = lineSeparator() +
            "=====" + lineSeparator() +
            "| some-title" + lineSeparator() +
            "-----" + lineSeparator() +
            "| row-1" + lineSeparator() +
            "| row-2" + lineSeparator() +
            "=====" + lineSeparator();

        String actual = StringTableFormatter.format(
            5,
            "some-title",
            ImmutableList.of("row-1", "row-2")
        );

        assertThat(actual).isEqualTo(expected);
    }

}
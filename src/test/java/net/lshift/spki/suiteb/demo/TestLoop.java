package net.lshift.spki.suiteb.demo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.PrintWriter;

import net.lshift.spki.InvalidInputException;
import net.lshift.spki.PrettyPrinter;
import net.lshift.spki.convert.openable.ByteOpenable;

import org.junit.Test;

public class TestLoop {
    @Test
    public void test() throws IOException, InvalidInputException {
        Service service = new Service("http", 80);
        ByteOpenable target = new ByteOpenable();
        WriteService.writeService(target, service);
        Service readBack = ReadService.readService(target);
        assertThat(readBack.name, is(service.name));
        assertThat(readBack.port, is(service.port));
        PrettyPrinter.prettyPrint(
            new PrintWriter(System.out), target.read());
    }
}

package net.lshift.spki.suiteb.demo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static net.lshift.spki.convert.openable.OpenableUtils.write;

import java.io.IOException;
import java.io.PrintWriter;

import net.lshift.spki.InvalidInputException;
import net.lshift.spki.ParseException;
import net.lshift.spki.PrettyPrinter;
import net.lshift.spki.convert.openable.ByteOpenable;
import org.junit.Test;

/**
 * You can consider the first part of each test the setting up of the system,
 * and key management. Then, the call to sendMessageFromServerToClient is the
 * system in actual operation.
 */
public class TestLoop {
    @Test
    public void serverSendsPlainTextMessageToClientThatTrustsIt()
            throws IOException, InvalidInputException {
        Server server = new Server();
        Client client = new Client();
        write(client.getAcl(), server.getPublicSigningKey());
        sendMessageFromServerToClient(server, client);
    }

    @Test
    public void serverSendsEncryptedMessageToClientThatTrustsIt()
            throws IOException, InvalidInputException {
        ServerWithEncryption server = new ServerWithEncryption();
        Client client = new Client();
        client.generateEncryptionKeypair();
        write(client.getAcl(), server.getPublicSigningKey());
        server.setRecipient(client.getPublicEncryptionKey());

        sendMessageFromServerToClient(server, client);
    }

    @Test
    public void serverSendsEncryptedMessageToClientThatTrustsMasterServer()
            throws IOException, InvalidInputException {
        final Master master = new Master();
        final PartiallyTrustedServer server = new PartiallyTrustedServer();
        master.delegateTrustTo(server.getCertificate(),
                server.getPublicSigningKey());

        final Client client = new Client();
        client.generateEncryptionKeypair();
        master.writeMasterTrust(client.getAcl());
        server.setRecipient(client.getPublicEncryptionKey());

        sendMessageFromServerToClient(server, client);
    }

    private void sendMessageFromServerToClient(Server server, Client client)
            throws IOException, InvalidInputException, ParseException {
        final Service service = new Service("http", 80);
        final ByteOpenable message = new ByteOpenable();
        server.writeServiceMessage(message, service);
        final Service readBack = client.receiveMessage(message);
        assertThat(readBack.name, is(service.name));
        assertThat(readBack.port, is(service.port));
        PrettyPrinter.prettyPrint(new PrintWriter(System.out), message.read());
    }
}

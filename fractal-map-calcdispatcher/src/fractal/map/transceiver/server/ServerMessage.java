package fractal.map.transceiver.server;

import java.util.UUID;

import fractal.map.transceiver.Transportable;

public class ServerMessage
{
	private UUID clientUUID;
	private Transportable body;

	public ServerMessage()
	{
	}

	public ServerMessage(UUID clientUUID, Transportable body)
	{
		this.clientUUID = clientUUID;
		this.body = body;
	}

	public UUID getClientUUID()
	{
		return clientUUID;
	}

	public void setClientUUID(UUID clientUUID)
	{
		this.clientUUID = clientUUID;
	}

	public Transportable getBody()
	{
		return body;
	}

	public void setBody(Transportable body)
	{
		this.body = body;
	}

	@Override
	public String toString()
	{
		return "ServerMessage [clientUUID=" + clientUUID + ", body=" + body + "]";
	}
}

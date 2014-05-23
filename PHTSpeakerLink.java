package PHT;

/** Represents a directional link between two PHTSpeakers. */
public class PHTSpeakerLink {
	public final PHTSpeaker source;
	public final PHTSpeaker destination;

	public PHTSpeakerLink(PHTSpeaker src, PHTSpeaker dst) {
		source = src;
		destination = dst;
	}
};
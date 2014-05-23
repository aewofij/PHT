package PHT;

/* Represents a transect sound with a set "goal" that the sound attempts
	to move towards. This should create straight paths whenever possible. */
public class PHTDirectedTransectSound extends PHTTransectSound {
	private final double[] _goal;

	/** Constructs a PHTDirectedTransectSound.
	 * 	
	 *	@param 	id 					the String ID of this sound.
	 * 	@param 	initialPosition 	the initial speaker for this sound.
	 * 	@param 	lifespan 			the duration of this sound, in milliseconds
	 *	@param 	goal 				the "goal" for this sound's path, a 3D array of { x, y, z } coordinates.
	 */
	public PHTDirectedTransectSound(String id, PHTSpeaker initialPosition, long lifespan, double[] goal) {
		super(id, initialPosition, lifespan);

		_goal = goal;
	}

	/** Chooses next speaker for sound to travel to. This overrides the chooseNextDestination() method in 
	 *		PHTTransectSound, adding the "goal" functionality.
	 * 
	 * 	@param 	from		the speaker from which the sound is traveling
	 *	@param	linkLayer	the ID for the link layer from which to draw the link to follow
	 */
	protected PHTSpeaker chooseNextDestination(PHTSpeaker from, String linkLayer) {
		PHTSpeaker result = from;

		for (PHTSpeakerLink link : from.linksMap().get(linkLayer)) {
			if (distance3D(link.destination.getPosition(), _goal) < distance3D(result.getPosition(), _goal)) {
				result = link.destination;
			}
		}

		return result;
	}

	/* ------ Helpers ------------------------------------- */

	private double distance3D(double[] p0, double[] p1) {
		if (p0.length != 3 || p1.length != 3) {
			return -1;
		}

		return distance3D(p0[0], p0[1], p0[2], p1[0], p1[1], p1[2]);
	}

	private double distance3D(double x0, double y0, double z0, double x1, double y1, double z1) {
		double deltaX = x1 - x0;
		double deltaY = y1 - y0;
		double deltaZ = z1 - z0;

		return (double)Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
	}
}
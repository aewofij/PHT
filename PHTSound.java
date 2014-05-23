package PHT;

import java.util.Map;
import com.cycling74.max.*;

/** Interface for representing a sound to be placed in a PHTSpeakerSet. */
public interface PHTSound {
	/** Updates this sound's speaker map, relying on computer's clock. 
	 *	@returns 	true if sound is still alive, else false.
	 */
	public boolean act();

	/** Returns a map of speakers to gain factor.
	 *  @returns 	a Map of PHTSpeaker to the corresponding gain factor (between 0. and 1.).
	 */
	public Map<PHTSpeaker, Double> speakerMap();

	/** Returns this sound's String ID. */
	public String id();

	/** Called on kill. 
	 * @returns 	an array of Atoms to output through outlet0, or null if no output
	 */
	public Atom[] kill();
}

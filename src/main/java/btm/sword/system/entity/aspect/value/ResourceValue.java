package btm.sword.system.entity.aspect.value;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceValue extends AspectValue {
    /** The base period (in ticks) between regeneration events. */
    private int regenPeriod;
    private float regenAmount;

    public ResourceValue(float value, int regenPeriod, float regenAmount) {
        super(value);
        this.regenPeriod = regenPeriod;
        this.regenAmount = regenAmount;
    }
}

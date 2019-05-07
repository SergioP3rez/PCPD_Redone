package structure;

import grafo.optilib.structure.InstanceFactory;

public class PCPDInstanceFactory extends InstanceFactory<PCPDInstance> {
    @Override
    public PCPDInstance readInstance(String s) {
        return new PCPDInstance(s);
    }
}

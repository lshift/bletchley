package net.lshift.spki.convert;

@Convert.ByPosition(name = "implementing-class", fields={})
public class ImplementingClass
    extends SexpBacked
    implements Interface
{
    public ImplementingClass()
    {
        super();
    }

    @Override
    public int hashCode()
    {
        return 1;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        return true;
    }
}

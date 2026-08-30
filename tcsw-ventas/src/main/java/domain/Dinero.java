package domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

// objeto de valor para no andar batallando con doubles en los calculos de dinero
public final class Dinero {

    private final BigDecimal monto;

    private Dinero(BigDecimal monto) {
        this.monto = monto;
    }

    public static Dinero de(double valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo");
        }
        BigDecimal redondeado = BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP);
        return new Dinero(redondeado);
    }

    public static Dinero cero() {
        return new Dinero(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    }

    public Dinero mas(Dinero otro) {
        return new Dinero(this.monto.add(otro.monto));
    }

    public Dinero por(int veces) {
        if (veces < 0) {
            throw new IllegalArgumentException("El multiplicador no puede ser negativo");
        }
        BigDecimal resultado = this.monto.multiply(BigDecimal.valueOf(veces)).setScale(2, RoundingMode.HALF_UP);
        return new Dinero(resultado);
    }

    public double valor() {
        return monto.doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dinero)) {
            return false;
        }
        Dinero otro = (Dinero) o;
        return monto.compareTo(otro.monto) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(monto.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return monto.toString();
    }
}

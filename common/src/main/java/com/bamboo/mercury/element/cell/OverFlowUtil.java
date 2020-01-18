package com.bamboo.mercury.element.cell;

import com.bamboo.mercury.api.DataType;
import com.bamboo.mercury.exception.DataOverFlowException;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class OverFlowUtil {

  public static final BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);

  public static final BigInteger MIN_INT = BigInteger.valueOf(Integer.MIN_VALUE);

  public static final BigInteger MAX_LONG = BigInteger
      .valueOf(Long.MAX_VALUE);

  public static final BigInteger MIN_LONG = BigInteger
      .valueOf(Long.MIN_VALUE);

  public static final BigDecimal MIN_DOUBLE_POSITIVE = new BigDecimal(
      String.valueOf(Double.MIN_VALUE));

  public static final BigDecimal MAX_DOUBLE_POSITIVE = new BigDecimal(
      String.valueOf(Double.MAX_VALUE));

  public static boolean isIntOverflow(final BigInteger integer) {
    return integer.compareTo(OverFlowUtil.MAX_INT) > 0
        || integer.compareTo(OverFlowUtil.MIN_INT) < 0;
  }

  public static void validateIntNotOverFlow(final BigInteger integer) {
    if (OverFlowUtil.isIntOverflow(integer)) {
      throw new DataOverFlowException(integer, DataType.INT);
    }
  }

  public static boolean isLongOverflow(final BigInteger integer) {
    return integer.compareTo(OverFlowUtil.MAX_LONG) > 0 || integer
        .compareTo(OverFlowUtil.MIN_LONG) < 0;

  }


  public static void validateLongNotOverFlow(final BigInteger integer) {
    if (OverFlowUtil.isLongOverflow(integer)) {
      throw new DataOverFlowException(integer, DataType.LONG);
    }
  }

  public static boolean isDoubleOverFlow(final BigDecimal decimal) {
    if (decimal.signum() == 0) {
      return false;
    }

    BigDecimal newDecimal = decimal;
    boolean isPositive = decimal.signum() == 1;
    if (!isPositive) {
      newDecimal = decimal.negate();
    }

    return (newDecimal.compareTo(MIN_DOUBLE_POSITIVE) < 0 || newDecimal
        .compareTo(MAX_DOUBLE_POSITIVE) > 0);
  }

  public static void validateDoubleNotOverFlow(final BigDecimal decimal) {
    if (OverFlowUtil.isDoubleOverFlow(decimal)) {
      throw new DataOverFlowException(decimal.toPlainString(), DataType.DOUBLE);
    }
  }
}

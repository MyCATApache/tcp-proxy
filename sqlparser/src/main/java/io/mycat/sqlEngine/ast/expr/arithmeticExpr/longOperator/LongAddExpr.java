package io.mycat.sqlEngine.ast.expr.arithmeticExpr.longOperator;

import io.mycat.sqlEngine.ast.expr.ValueExpr;
import io.mycat.sqlEngine.ast.expr.numberExpr.LongExpr;
import io.mycat.sqlEngine.context.RootSessionContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LongAddExpr implements LongExpr {

  private final RootSessionContext context;
  private final ValueExpr left;
  private final ValueExpr right;

  @Override
  public Long getValue() {
    Long left = (Long) this.left.getValue();
    if (left == null){
      return null;
    }
    Long rightValue = (Long) this.right.getValue();
    if (rightValue == null){
      return null;
    }
    return left + rightValue;
  }
}
package io.mycat.sqlhandler.dql;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowStatusStatement;
import io.mycat.MycatDataContext;
import io.mycat.sqlhandler.AbstractSQLHandler;
import io.mycat.sqlhandler.SQLRequest;
import io.mycat.Response;
import io.vertx.core.impl.future.PromiseInternal;


public class ShowStatusSQLHandler extends AbstractSQLHandler<MySqlShowStatusStatement> {

    @Override
    protected PromiseInternal<Void> onExecute(SQLRequest<MySqlShowStatusStatement> request, MycatDataContext dataContext, Response response) throws Exception {
        return response.proxySelectToPrototype(request.getAst().toString());
    }
}

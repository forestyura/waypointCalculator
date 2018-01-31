package domains;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import logginig.Logger;

public abstract class PersistentContainer<T extends PersistentObject> {
    protected static PersistentContainer<? extends PersistentObject> instance;
    protected static Logger logger;
    protected List<PersistentObject> entityList = new ArrayList();
    protected static List<DataChangeListener> listeners = new ArrayList();

    public void saveAll()
            throws SQLException {
        Iterator localIterator = this.entityList.iterator();
        while (localIterator.hasNext()) {
            PersistentObject localPersistentObject = (PersistentObject) localIterator.next();
            localPersistentObject.save();
        }
    }

    public static void addDataChangedListener(DataChangeListener paramDataChangeListener) {
        listeners.add(paramDataChangeListener);
    }

    protected static void notifyListeners() {
        Iterator localIterator = listeners.iterator();
        while (localIterator.hasNext()) {
            DataChangeListener localDataChangeListener = (DataChangeListener) localIterator.next();
            localDataChangeListener.dataChanged();
        }
    }
}

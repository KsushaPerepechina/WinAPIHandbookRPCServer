namespace java thrift

struct WinAPIFunction {
    1: i32 id;
    2: string name;
    3: string description;
    4: string params;
    5: string returnValue;
}

service WinAPIHandbookService {
    list<WinAPIFunction> getAllFunctions();
    void addFunction(1: WinAPIFunction func);
    void removeFunction(1: WinAPIFunction func);
    void updateFunction(1:WinAPIFunction func);
}

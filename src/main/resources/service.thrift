namespace java thrift

struct WinAPIFunction {
    1: i32 id;
    2: string name;
    3: string params;
    4: string returnValue;
    5: string description;
}

service WinAPIHandbookService {
    void addFunction(1: WinAPIFunction func);
    list<WinAPIFunction> getAllFunctions();
    void updateFunction(1:WinAPIFunction func);
    void removeFunction(1: WinAPIFunction func);
}

namespace java thrift

struct WinAPITechnologyVersions {
    1: string versionWin16;
    2: string versionWin32;
    3: string versionWin32s;
    4: string versionWin64;
}

struct WinAPITechnology {
    1: i32 id;
    2: string name;
    3: WinAPITechnologyVersions versions;
    4: string description;
}

service WinAPIHandbookService {
    list<WinAPITechnology> getAllTechnologies();
    void addTechnology(1: WinAPITechnology technology);
    void removeTechnology(1: WinAPITechnology technology);
    void updateTechnology(1:WinAPITechnology technology);
}

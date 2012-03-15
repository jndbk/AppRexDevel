package rex.login.service; 

import rex.login.AppInfo;
interface IAppService{
	List<AppInfo> getAppInfo();
	void clearAppInfo();
}

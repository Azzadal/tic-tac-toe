import AxiosInstance from '../../axios-instance';

class SessionService {
  public async getSessions(options?: { active: boolean }): Promise<any[]> {
    const { data } = await AxiosInstance.get('/sessions', {
      params: {
        active: options?.active,
      },
    });
    return data;
  }
}

export default new SessionService();

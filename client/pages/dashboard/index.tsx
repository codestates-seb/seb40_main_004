import { NextPage } from 'next';
import { Header } from '../../components/common/Header';

const Dashboard: NextPage = () => {
  return (
    <>
      <Header />
      <main className="max-w-[1280px] mx-auto">
        <h1>Dashboard</h1>
      </main>
    </>
  );
};

export default Dashboard;

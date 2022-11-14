import { NextPage } from 'next';
import { Footer } from '../components/common/Footer';
import { Header } from '../components/common/Header';

const Home: NextPage = () => {
  return (
    <>
      <main className="max-w-[1280px] mx-auto min-h-screen">
        <Header />
        <h1>Main</h1>
      </main>
      <Footer />
    </>
  );
};

export default Home;

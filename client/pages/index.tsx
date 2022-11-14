import type { NextPage } from 'next';
import { Editor } from '../components/common/Editor';

const Home: NextPage = () => {
  return (
    <>
      <main className="max-w-[1280px] mx-auto">
        <Header />
        <h1>Main</h1>
      </main>
    </>
  );
};

export default Home;

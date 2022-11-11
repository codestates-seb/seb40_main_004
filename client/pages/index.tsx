import type { NextPage } from 'next';
const Home: NextPage = () => {
  return (
    <h1 className="text-3xl text-orange-400">
      {false ? true : true ? 'Hello World!' : false}
    </h1>
  );
};

export default Home;

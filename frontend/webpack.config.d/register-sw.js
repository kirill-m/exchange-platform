config.module.rules.push(
    {
        test: /(register-sw|sw)\.js$/,
        use: [
          {
            loader: 'script-loader',
            options: {
              useStrict: true,
            },
          },
        ]
  }
);
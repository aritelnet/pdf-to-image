const { Stack, Duration } = require('aws-cdk-lib');
const lambda = require('aws-cdk-lib/aws-lambda');
const iam = require('aws-cdk-lib/aws-iam');
const apigatewayv2 = require('aws-cdk-lib/aws-apigatewayv2');
const apigwi = require('aws-cdk-lib/aws-apigatewayv2-integrations');
const path = require('path');

class AwsStack extends Stack {
  /**
   *
   * @param {Construct} scope
   * @param {string} id
   * @param {StackProps=} props
   */
  constructor(scope, id, props) {
    super(scope, id, props);

    // The code that defines your stack goes here

    // example resource
    // const queue = new sqs.Queue(this, 'AwsQueue', {
    //   visibilityTimeout: Duration.seconds(300)
    // });

    // Lambda 実行ロールの定義
    const lambdaExecutionRole = new iam.Role(this, 'LambdaExecutionRole', {
      assumedBy: new iam.ServicePrincipal('lambda.amazonaws.com'),
      managedPolicies: [
        iam.ManagedPolicy.fromAwsManagedPolicyName('service-role/AWSLambdaBasicExecutionRole'),
      ],
    });

    // Lambda 関数の作成
    const pdfToImageLambda = new lambda.Function(this, 'PdfToImageFunction', {
      runtime: lambda.Runtime.JAVA_17,
      architecture: lambda.Architecture.ARM_64,
      handler: 'net.aritel.tools.pdf2image.PdfToImageHandler::handleRequest',
      code: lambda.Code.fromAsset(path.join(__dirname, '../../target/pdf-to-image-0.0.3.jar')),
      memorySize: 512,
      timeout: Duration.seconds(30),
      role: lambdaExecutionRole,
    });

    // API Gateway の作成
    const api = new apigatewayv2.HttpApi(this, 'PdfToImageHttpApi', {
      apiName: 'PdfToImage-HttpApi',
      description: 'This service converts PDF files to images.',
    });

    // POST メソッドを作成し、Lambda 関数を統合
    new apigatewayv2.HttpRoute(this, 'PdfToImageRoute', {
      httpApi: api,
      routeKey: apigatewayv2.HttpRouteKey.with('/pdf-to-image', apigatewayv2.HttpMethod.POST),
      integration: new apigwi.HttpLambdaIntegration('PdfToImageIntegration', pdfToImageLambda)
    })
  }
}

module.exports = { AwsStack }
